package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.auth.Authenticator;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.protocol.ElectricDataResponse;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.MeterDataResponse.PiggyBackMessage;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static edu.nccu.cs.protocol.Constants.PATH_ELECTRIC_DATA;
import static edu.nccu.cs.protocol.MeterDataResponse.MessageType.*;

@RestController
@Slf4j
public class ElectricDataController {

    @Autowired
    private ReceiverService receiverService;

    @Autowired
    private VerifierService verifierService;

    @Autowired
    private QuerierService querierService;

    @Autowired
    private Authenticator authenticator;

    @GetMapping(PATH_ELECTRIC_DATA)
    public ElectricDataResponse<SignedMeterDataEntity, FixDataEntity> getElectricData(
            @RequestParam("date") String date) {
        LocalDate ld = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<SignedMeterDataEntity> meterData = querierService.getMeterDataByDate(ld);
        List<FixDataEntity> fixData = querierService.getFixDataByDate(ld);

        return ElectricDataResponse.<SignedMeterDataEntity, FixDataEntity>builder()
                                   .serverTime(System.currentTimeMillis())
                                   .meterData(meterData)
                                   .fixData(fixData)
                                   .build();
    }

    @PostMapping(PATH_ELECTRIC_DATA)
    public MeterDataResponse receive(MeterDataRequest<SignedMeterDataRequest> request) {
        log.warn("received request: {}", request);

        if (!authenticator.checkIdAndToken(request.getApplicationId(), request.getToken())) {
            return getNotAuthorizedResponse();
        }

        List<SignedMeterDataEntity> entities = handleRequest(request, request.getPayload());

        receiverService.saveAll(entities);

        List<Long> fixTimestamp = getFixTimestamp();

        return MeterDataResponse.builder()
                                .cloudTime(System.currentTimeMillis())
                                .message(PiggyBackMessage.builder()
                                                         .type(fixTimestamp.isEmpty() ? SUCCESS : FIX)
                                                         .payload(fixTimestamp)
                                                         .build())
                                .build();
    }

    public MeterDataResponse getNotAuthorizedResponse() {
        return MeterDataResponse.builder()
                                .cloudTime(System.currentTimeMillis())
                                .message(PiggyBackMessage.builder()
                                                         .type(NOT_AUTHORIZED)
                                                         .payload(null)
                                                         .build())
                                .build();
    }

    public List<SignedMeterDataEntity> handleRequest(MeterDataRequest<?> request, List<SignedMeterDataRequest> payload) {
        return payload.stream()
                      .map(p -> receiverService.buildInitEntity(request.getApplicationId(), p))
                      .collect(Collectors.toList());
    }

    public List<Long> getFixTimestamp() {
        List<FixDataEntity> fixData = verifierService.getInitFixData();

        if (!fixData.isEmpty()) {
            verifierService.updateFixDataToWait(fixData);
        }

        return fixData.stream()
                      .map(FixDataEntity::getTimestamp)
                      .collect(Collectors.toList());
    }
}
