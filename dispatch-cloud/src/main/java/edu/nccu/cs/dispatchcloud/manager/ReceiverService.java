package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static edu.nccu.cs.utils.DateTimeUtils.getDefault;
import static edu.nccu.cs.utils.DateTimeUtils.getNow;

@Service
@Slf4j
public class ReceiverService {

    @Autowired
    private SignedMeterDataRepository repository;
    @Autowired
    private VerifierService verifierService;

    public void save(SignedMeterDataEntity entity) {
        repository.save(entity);
    }

    public void saveAll(List<SignedMeterDataEntity> entities) {

        List<SignedMeterDataEntity> result = saveSignedMeterData(entities);

        createFixData(result);

        updateFixDataToDone(result);
    }

    public void createFixData(List<SignedMeterDataEntity> result) {
        Set<Long> fixing =
                result.stream()
                      .filter(entity -> Objects.equals(CheckState.FIX, entity.getCheckState()))
                      .map(SignedMeterDataEntity::getPreTimestamp)
                      .collect(Collectors.toSet());

        verifierService.createFixData(fixing);
    }

    public void updateFixDataToDone(List<SignedMeterDataEntity> result) {
        Set<Long> done =
                result.stream()
                      .filter(entity -> Objects.equals(CheckState.DONE, entity.getCheckState()))
                      .map(SignedMeterDataEntity::getPreTimestamp)
                      .collect(Collectors.toSet());

        verifierService.updateToDone(done);
    }

    private List<SignedMeterDataEntity> saveSignedMeterData(List<SignedMeterDataEntity> entities) {
        Set<Long> currTimestamp = entities.stream()
                                          .map(SignedMeterDataEntity::getTimestamp)
                                          .collect(Collectors.toSet());

        List<SignedMeterDataEntity> sorted =
                entities.stream()
                        .sorted(Comparator.comparing(SignedMeterDataEntity::getPreTimestamp))
                        .peek(this::checkFixEntity)
                        .peek(entity -> this.checkFixState(entity, currTimestamp))
                        .collect(Collectors.toList());

        repository.saveAll(sorted);

        return sorted;
    }

    /**
     * 檢查傳進來的是否有補值
     *
     * @param income
     */
    private void checkFixEntity(SignedMeterDataEntity income) {
        Optional<SignedMeterDataEntity> fixEntity = repository.findByPreTimestampAndCheckState(income.getTimestamp(), CheckState.FIX);

        if (fixEntity.isPresent()) {
            BeanUtils.copyProperties(fixEntity, income);

            income.setCheckState(CheckState.DONE);
            income.setDoneTime(getNow());
        }
    }

    /**
     * 檢查傳進來的是否有缺 preTimestamp 的值
     *
     * @param income
     */
    private void checkFixState(SignedMeterDataEntity income, Set<Long> currTimestamp) {
        if (income.getPreTimestamp() == SignedMeterDataEntity.FIRST_PRETIMESTAMP) {
            income.setCheckState(CheckState.DONE);
            income.setDoneTime(getNow());
        } else if (income.getCheckState() == CheckState.INIT) {
            Optional<SignedMeterDataEntity> preEntity = repository.findByTimestamp(income.getPreTimestamp());

            Optional<Long> memEntity = currTimestamp.stream()
                                                    .filter(timestamp -> Objects.equals(timestamp, income.getPreTimestamp()))
                                                    .findAny();

            if (preEntity.isEmpty() && memEntity.isEmpty()) {
                income.setCheckState(CheckState.FIX);
                income.setFixTime(getNow());
            } else {
                income.setCheckState(CheckState.DONE);
                income.setDoneTime(getNow());
            }
        }
    }

    public SignedMeterDataEntity buildInitEntity(String applicationId, SignedMeterDataRequest payload) {
        return SignedMeterDataEntity.builder()
                                    .edgeId(applicationId)
                                    .timestamp(payload.getTimestamp())
                                    .preTimestamp(payload.getPreTimestamp())
                                    .energy(payload.getEnergy())
                                    .power(payload.getPower())
                                    .signature(payload.getSignature())
                                    .checkState(CheckState.INIT)
                                    .initTime(getNow())
                                    .fixTime(getDefault())
                                    .doneTime(getDefault())
                                    .build()
                                    .init();
    }
}
