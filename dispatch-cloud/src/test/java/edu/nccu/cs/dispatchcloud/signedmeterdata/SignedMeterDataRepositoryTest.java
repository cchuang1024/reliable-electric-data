package edu.nccu.cs.dispatchcloud.signedmeterdata;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState.INIT;
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
        long timestamp = System.currentTimeMillis();

        SignedMeterDataEntity entity = prepareEntity(timestamp);

        repository.save(entity);

        Optional<SignedMeterDataEntity> opEntity = repository.findByTimestamp(timestamp);

        assertThat(opEntity).isPresent();

        repository.deleteAll();
    }

    private SignedMeterDataEntity prepareEntity(long timestamp) {
        return SignedMeterDataEntity.builder()
                                    .timestamp(timestamp)
                                    .preTimestamp(0L)
                                    .power(0L)
                                    .energy(0L)
                                    .signature("")
                                    .edgeId("")
                                    .checkState(INIT)
                                    .build();
    }
}
