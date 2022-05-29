package edu.nccu.cs.datasender.signedmeterdata;

import edu.nccu.cs.datasender.common.HttpSender;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SignedMeterDataService {

    @Autowired
    private SignedMeterDataRepository repository;

    @Autowired
    private HttpSender sender;

    public List<SignedMeterDataEntity> getInitEntities() {
        return repository.findByState(SignedMeterDataEntity.STATE_INIT);
    }

    public List<SignedMeterDataEntity> getPendingEntities() {
        return repository.findByState(SignedMeterDataEntity.STATE_PENDING);
    }

    public MeterDataResponse sendData(String applicationId, String token, List<SignedMeterDataEntity> entities) {
        List<SignedMeterDataRequest> payload =
                entities.stream()
                        .map(this::buildPayload)
                        .collect(Collectors.toList());

        MeterDataRequest<SignedMeterDataRequest> request =
                MeterDataRequest.<SignedMeterDataRequest>builder()
                                .applicationId(applicationId)
                                .token(token)
                                .payload(payload)
                                .build();

        return sender.sendMeterData(request);
    }

    private SignedMeterDataRequest buildPayload(SignedMeterDataEntity entity) {
        return SignedMeterDataRequest.builder()
                                     .energy(entity.getEnergy())
                                     .power(entity.getPower())
                                     .timestamp(entity.getTimestamp())
                                     .preTimestamp(entity.getPreTimestamp())
                                     .signature(entity.getSignature())
                                     .build();
    }

    public void updateToPending(List<Long> timestamps) {
        List<SignedMeterDataEntity> entities = repository.findByTimestampIn(new HashSet<>(timestamps));

        entities.forEach(entity -> {
            entity.setState(SignedMeterDataEntity.STATE_PENDING);
            repository.save(entity);
        });
    }

    public void updateToDone(List<Long> timestamps) {
        List<SignedMeterDataEntity> entities = repository.findByTimestampIn(new HashSet<>(timestamps));

        entities.forEach(entity -> {
            entity.setState(SignedMeterDataEntity.STATE_DONE);
            repository.save(entity);
        });
    }
}
