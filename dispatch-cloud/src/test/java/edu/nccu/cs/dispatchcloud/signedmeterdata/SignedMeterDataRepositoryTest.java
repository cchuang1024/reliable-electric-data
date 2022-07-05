package edu.nccu.cs.dispatchcloud.signedmeterdata;

import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState.INIT;
import static edu.nccu.cs.utils.DateTimeUtils.toInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@SpringBootTest
@Slf4j
public class SignedMeterDataRepositoryTest {

    @Autowired
    private SignedMeterDataRepository repository;

    @Test
    public void testLoadContext() {
        assertThat(repository).isNotNull();
    }

    @Test
    public void testFilterToFix() {
        long dayFirstTimestamp = DateTimeUtils.timestampFromLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        long nowTimestamp = DateTimeUtils.timestampFromLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        List<SignedMeterDataEntity> results = repository.findByPreTimestampBetween(dayFirstTimestamp, nowTimestamp);

        Set<Long> preTimestamps = results.stream()
                .map(SignedMeterDataEntity::getPreTimestamp)
                .collect(Collectors.toSet());

        Long firstPreTimestamp = Collections.min(preTimestamps);
        Long lastPreTimestamp = Collections.max(preTimestamps);
        List<SignedMeterDataEntity> result2 = repository.findByTimestampBetween(firstPreTimestamp, lastPreTimestamp);

        Set<Long> timestamps = result2.stream()
                .map(SignedMeterDataEntity::getTimestamp)
                .collect(Collectors.toSet());

        Set<Long> result3 = preTimestamps.stream()
                .filter(pre -> !timestamps.contains(pre))
                .collect(Collectors.toSet());
        log.info("filter result {}", result3);
    }

    @Test
    public void testFindByTimestamp() {
        long timestamp = toInstant(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)).toEpochMilli();

        SignedMeterDataEntity entity = prepareEntity(timestamp);

        repository.save(entity);

        Optional<SignedMeterDataEntity> opEntity = repository.findByTimestamp(timestamp);

        assertThat(opEntity).isPresent();

        // repository.deleteAll();
    }

    @Test
    public void testDeleteAll() {
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

    private SignedMeterDataEntity prepareEntity(long timestamp) {
        return SignedMeterDataEntity.builder()
                .timestamp(timestamp)
                .preTimestamp(0L)
                .power(1000L)
                .energy(10L)
                .signature("")
                .edgeId("")
                .checkState(INIT)
                .build();
    }
}
