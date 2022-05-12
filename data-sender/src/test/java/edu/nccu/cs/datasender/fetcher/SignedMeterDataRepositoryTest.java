package edu.nccu.cs.datasender.fetcher;

import java.util.List;

import com.google.common.collect.Lists;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class SignedMeterDataRepositoryTest {

    @Autowired
    private SignedMeterDataRepository repository;

    @Test
    public void testFindAll() {
        List<SignedMeterDataEntity> entities = Lists.newArrayList(repository.findAll());
        Assertions.assertThat(entities).isNotNull();
        log.info("entities: {}", entities);
        for (SignedMeterDataEntity entity : entities) {
            log.info("entity energy: {}", entity.getEnergy());
        }
    }

    @Test
    public void testFindByInitialed() {
        List<SignedMeterDataEntity> entities = Lists.newArrayList(repository.findByState(SignedMeterDataEntity.STATE_INIT));
        log.info("entities: {}", entities);
    }
}
