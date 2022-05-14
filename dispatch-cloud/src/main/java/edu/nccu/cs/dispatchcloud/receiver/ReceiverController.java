package edu.nccu.cs.dispatchcloud.receiver;

import edu.nccu.cs.dispatchcloud.auth.Authenticator;
import edu.nccu.cs.dispatchcloud.verifier.FixDataEntity;
import edu.nccu.cs.dispatchcloud.verifier.VerifierService;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static edu.nccu.cs.protocol.Constants.PATH_ELECTRIC_DATA;
import static edu.nccu.cs.protocol.MeterDataResponse.MessageType.*;

@RestController
@Slf4j
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @Autowired
    private VerifierService verifierService;

    @Autowired
    private Authenticator authenticator;

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
                                .message(MeterDataResponse.PiggyBackMessage.builder()
                                                                           .type(fixTimestamp.isEmpty() ? SUCCESS : FIX)
                                                                           .payload(fixTimestamp)
                                                                           .build())
                                .build();
    }

    public MeterDataResponse getNotAuthorizedResponse() {
        return MeterDataResponse.builder()
                                .cloudTime(System.currentTimeMillis())
                                .message(MeterDataResponse.PiggyBackMessage.builder()
                                                                           .type(NOT_AUTHORIZED)
                                                                           .payload(null)
                                                                           .build())
                                .build();
    }

    public List<SignedMeterDataEntity> handleRequest(MeterDataRequest<?> request, List<SignedMeterDataRequest> payload) {
        return payload.stream()
                      .map(p -> SignedMeterDataEntity.builder()
                                                     .edgeId(request.getApplicationId())
                                                     .timestamp(p.getTimestamp())
                                                     .preTimestamp(p.getPreTimestamp())
                                                     .energy(p.getEnergy())
                                                     .power(p.getPower())
                                                     .signature(p.getSignature())
                                                     .build())
                      .collect(Collectors.toList());
    }

    public List<Long> getFixTimestamp() {
        return verifierService.getInitFixData()
                              .stream()
                              .map(FixDataEntity::getTimestamp)
                              .collect(Collectors.toList());
    }
}
