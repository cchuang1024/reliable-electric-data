package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SelfVerifierJob implements Runnable {

    @Autowired
    private ReceiverService receiverService;
    @Autowired
    private VerifierService verifierService;
    @Autowired
    private QuerierService querierService;

    @Value("${application.verifyPeriod}")
    private Integer verifyPeriod;

    @Override
    public void run() {
        log.info("self verify fixing data.");

        LocalDateTime currentMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        long verifyEnd = DateTimeUtils.timestampFromLocalDateTime(currentMinute);
        // long verifyStart = DateTimeUtils.timestampFromLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        long verifyStart = DateTimeUtils.timestampFromLocalDateTime(currentMinute.minusHours(verifyPeriod));
        log.info("find meter data by preTimestamp from {} to {}", verifyStart, verifyEnd);

        List<SignedMeterDataEntity> preTimestampUntilNow = querierService.getMeterDataBetweenPreTimestamp(verifyStart, verifyEnd);
        if (preTimestampUntilNow.isEmpty()) {
            return;
        }

        Set<Long> preTimestamps = preTimestampUntilNow.stream()
                .map(SignedMeterDataEntity::getPreTimestamp)
                .collect(Collectors.toSet());

        Long firstPreTimestamp = Collections.min(preTimestamps);
        Long lastPreTimestamp = Collections.max(preTimestamps);
        log.info("find meter data by timestamp from {} to {}", firstPreTimestamp, lastPreTimestamp);

        List<SignedMeterDataEntity> timestampUntilNow = querierService.getMeterDataBetweenTimestamp(firstPreTimestamp, lastPreTimestamp);
        Set<Long> timestamps = timestampUntilNow.stream()
                .map(SignedMeterDataEntity::getTimestamp)
                .collect(Collectors.toSet());

        preTimestamps.removeAll(timestamps);

        if (!preTimestamps.isEmpty()) {
            log.info("preTimestamps need to be fixed: {}", preTimestamps);
            receiverService.updateDataToFix(preTimestamps);
            verifierService.createFixDataWithRenew(preTimestamps);
        }
    }
}
