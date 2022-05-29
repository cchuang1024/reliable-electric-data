package edu.nccu.cs.dispatchcloud.signedmeterdata;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState.INIT;
import static edu.nccu.cs.utils.DateTimeUtils.toInstant;
import static org.assertj.core.api.Assertions.assertThat;

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
