package edu.nccu.cs.recorder.fetcher;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class SignedMeterRepositoryTest {

    @Autowired
    private SignedMeterRepository repository;

    @Test
    public void testSaveAndGet() {
        SignedMeterData meterData = prepareMeterData();
        long timestamp = DateTimeUtils.timestampFromLocalDateTime(LocalDateTime.of(2022, 3, 21, 23, 38, 0));

        repository.save(SignedMeterEntity.getInstanceByInstantAndData(Instant.ofEpochMilli(timestamp), meterData));

        Optional<SignedMeterEntity> mbEntity = repository.findByTimestamp(timestamp);

        assertThat(mbEntity).isPresent();

        log.info("loaded entity: {}", mbEntity.get());
    }

    private SignedMeterData prepareMeterData() {
        return SignedMeterData.builder()
                              .meterData(MeterData.builder()
                                                  .power(0)
                                                  .energy(0)
                                                  .build())
                              .signature("")
                              .build();
    }
}
