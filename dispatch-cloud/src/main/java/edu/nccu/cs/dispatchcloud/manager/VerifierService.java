package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState.INIT;
import static edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState.WAIT;
import static edu.nccu.cs.utils.DateTimeUtils.getDefault;
import static edu.nccu.cs.utils.DateTimeUtils.getNow;

@Service
@Slf4j
public class VerifierService {

    @Autowired
    private FixDataRepository fixDataRepository;
    @Autowired
    private SignedMeterDataRepository meterDataRepository;

    public List<FixDataEntity> getInitFixData() {
        return fixDataRepository.findByState(INIT);
    }

    public List<FixDataEntity> getWaitFixData() {
        return fixDataRepository.findByState(WAIT);
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
        fixData.forEach(timestamp ->
                fixDataRepository.save(FixDataEntity.builder()
                                                    .timestamp(timestamp)
                                                    .state(INIT)
                                                    .initTime(getNow())
                                                    .waitTime(getDefault())
                                                    .doneTime(getDefault())
                                                    .build()
                                                    .init()));
    }

    public void updateToDone(Set<Long> doneTimestamps) {
        List<FixDataEntity> waitEntities =
                fixDataRepository.findByTimestampInAndState(doneTimestamps, WAIT);

        for (FixDataEntity wait : waitEntities) {
            wait.setState(FixState.DONE);
            wait.setDoneTime(getNow());

            fixDataRepository.save(wait);
        }
    }
}
