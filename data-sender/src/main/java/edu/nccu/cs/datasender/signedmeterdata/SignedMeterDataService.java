package edu.nccu.cs.datasender.signedmeterdata;

import edu.nccu.cs.datasender.common.HttpSender;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
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

    public void setRepository(SignedMeterDataRepository repository) {
        this.repository = repository;
    }

    public List<SignedMeterDataEntity> getInitEntities() {
        return repository.findByState(SignedMeterDataEntity.STATE_INIT);
    }

    public List<SignedMeterDataEntity> getPendingEntities() {
        return repository.findByState(SignedMeterDataEntity.STATE_PENDING);
    }

    public MeterDataResponse sendData(String applicationId, String token, Integer maxData, List<SignedMeterDataEntity> entities) {
        if (entities.size() > maxData) {
            return splitAndSend(applicationId, token, maxData, entities);
        } else {
            return directlySend(applicationId, token, entities);
        }
    }

    private MeterDataResponse splitAndSend(String applicationId, String token, Integer maxData, List<SignedMeterDataEntity> entities) {
        MeterDataResponse response;

        int sendTimes = calculateSendTimes(maxData, entities.size());
        for (int i = 0; i < sendTimes; i++) {
            int start = i * maxData;
            int boundary = Math.min(((i + 1) * maxData), entities.size());

            List<SignedMeterDataEntity> subEntities = entities.subList(start, boundary);

            response = directlySend(applicationId, token, subEntities);


        }
        return null;
    }

    public int calculateSendTimes(Integer maxData, Integer entitySize) {
        int remain = entitySize % maxData;
        int times = entitySize / maxData;
        return times + (remain > 0 ? 1 : 0);
    }

    public MeterDataResponse directlySend(String applicationId, String token, List<SignedMeterDataEntity> entities) {
        log.info("directly send to cloud.");
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

    public List<SignedMeterDataEntity> collectSendList() {
        List<SignedMeterDataEntity> initEntities = getInitEntities();
        List<SignedMeterDataEntity> pendingEntities = getPendingEntities();

        List<SignedMeterDataEntity> sendList = new ArrayList<>();
        sendList.addAll(initEntities);
        sendList.addAll(pendingEntities);

        return sendList;
    }

    public List<SignedMeterDataEntity> collectLimitedSendList(Integer maxData) {
        List<SignedMeterDataEntity> allData = collectSendList();

        List<SignedMeterDataEntity> sorted = allData.stream()
                                                    .sorted(Comparator.comparing(SignedMeterDataEntity::getTimestamp, Comparator.reverseOrder()))
                                                    .collect(Collectors.toList());
        
        return sorted.subList(0, Math.min(maxData, sorted.size()));
    }
}
