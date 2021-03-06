package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState.*;
import static edu.nccu.cs.utils.DateTimeUtils.getDefault;
import static edu.nccu.cs.utils.DateTimeUtils.getNow;

@Service
@Slf4j
public class VerifierService {

    @Autowired
    private FixDataRepository fixDataRepository;

    @Value("${application.waitLimit}")
    private Integer waitLimit;

    public List<FixDataEntity> getInitFixData() {
        return fixDataRepository.findByState(INIT);
    }

    public List<FixDataEntity> getOutOfLimitWaitData() {
        long waitLimitMillis = waitLimit * 60 * 1000L;
        Instant currentMinute = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        long outOfLimitTimestamp = currentMinute.toEpochMilli() - waitLimitMillis;

        return fixDataRepository.findByStateAndTimestampBefore(WAIT, outOfLimitTimestamp);
    }

    public void updateFixDataToWait(List<FixDataEntity> fixData) {
        fixDataRepository.saveAll(fixData.stream()
                .peek(fix -> {
                    fix.setState(FixState.WAIT);
                    fix.setWaitTime(getNow());
                })
                .collect(Collectors.toList()));
    }

    public void createFixData(Set<Long> fixData) {
        for (Long timestamp : fixData) {
            Optional<FixDataEntity> origFixData = fixDataRepository.findByTimestamp(timestamp);
            if (origFixData.isEmpty()) {
                FixDataEntity newFixData = FixDataEntity.builder()
                        .timestamp(timestamp)
                        .state(INIT)
                        .initTime(getNow())
                        .waitTime(getDefault())
                        .doneTime(getDefault())
                        .build()
                        .init();
                fixDataRepository.save(newFixData);
            }
        }
    }

    public void createFixDataWithRenew(Set<Long> fixData) {
        for (Long timestamp : fixData) {
            Optional<FixDataEntity> origFixData = fixDataRepository.findByTimestamp(timestamp);
            if (origFixData.isEmpty()) {
                FixDataEntity newFixData = FixDataEntity.builder()
                        .timestamp(timestamp)
                        .state(INIT)
                        .initTime(getNow())
                        .waitTime(getDefault())
                        .doneTime(getDefault())
                        .build()
                        .init();
                fixDataRepository.save(newFixData);
            } else {
                FixDataEntity origFix = origFixData.get();
                if (Objects.equals(DONE, origFix.getState())) {
                    origFix.setState(INIT);
                    origFix.setInitTime(getNow());
                    fixDataRepository.save(origFix);
                }
            }
        }
    }

    public void updateToDone(Set<Long> doneTimestamps) {
        List<FixDataEntity> waitEntities =
                fixDataRepository.findByTimestampInAndState(doneTimestamps, WAIT);

        for (FixDataEntity wait : waitEntities) {
            wait.setState(DONE);
            wait.setDoneTime(getNow());

            fixDataRepository.save(wait);
        }
    }
}
