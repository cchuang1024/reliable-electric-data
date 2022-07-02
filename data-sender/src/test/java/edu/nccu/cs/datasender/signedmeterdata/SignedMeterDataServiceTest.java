package edu.nccu.cs.datasender.signedmeterdata;

import com.google.common.collect.ImmutableList;
import edu.nccu.cs.datasender.manager.ApplicationState;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class SignedMeterDataServiceTest {

    @Autowired
    private SignedMeterDataService service;
    @Autowired
    private ApplicationState applicationState;

    @Test
    public void testGetPendingEntities(){
        List<SignedMeterDataEntity> pending = service.getPendingEntities();
        log.info("pending list: {}", pending);
    }

    @Test
    public void testCollectSendList(){
        List<SignedMeterDataEntity> sendList = service.collectSendList();
        log.info("send list: {}", sendList);
    }

    @Test
    public void testCollectLimitedSendList() {
        SignedMeterDataRepository repository = Mockito.mock(SignedMeterDataRepository.class);
        Mockito.when(repository.findByState(SignedMeterDataEntity.STATE_INIT)).thenReturn(prepareInitData());
        Mockito.when(repository.findByState(SignedMeterDataEntity.STATE_PENDING)).thenReturn(preparePendingData());
        service.setRepository(repository);

        List<SignedMeterDataEntity> origData = service.collectSendList();
        log.info("orig data: {}", origData);

        List<SignedMeterDataEntity> sendData = service.collectLimitedSendList(applicationState.getMaxData());

        Assertions.assertThat(sendData).hasSize(applicationState.getMaxData());
        int i = 20;
        for (SignedMeterDataEntity data : sendData) {
            Assertions.assertThat(data.getTimestamp()).isEqualTo(i--);
        }
    }

    private List<SignedMeterDataEntity> prepareInitData() {
        return ImmutableList.<SignedMeterDataEntity>builder()
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(20L)
                                                      .power(20L)
                                                      .timestamp(20L)
                                                      .preTimestamp(19L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(19L)
                                                      .power(19L)
                                                      .timestamp(19L)
                                                      .preTimestamp(18L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(18L)
                                                      .power(18L)
                                                      .timestamp(18L)
                                                      .preTimestamp(17L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(17L)
                                                      .power(17L)
                                                      .timestamp(17L)
                                                      .preTimestamp(16L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(16L)
                                                      .power(16L)
                                                      .timestamp(16L)
                                                      .preTimestamp(15L)
                                                      .build())
                            .build();
    }

    private List<SignedMeterDataEntity> preparePendingData() {
        return ImmutableList.<SignedMeterDataEntity>builder()
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(15L)
                                                      .power(15L)
                                                      .timestamp(15L)
                                                      .preTimestamp(14L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(14L)
                                                      .power(14L)
                                                      .timestamp(14L)
                                                      .preTimestamp(13L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(13L)
                                                      .power(13L)
                                                      .timestamp(13L)
                                                      .preTimestamp(12L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(12L)
                                                      .power(12L)
                                                      .timestamp(12L)
                                                      .preTimestamp(11L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(11L)
                                                      .power(11L)
                                                      .timestamp(11L)
                                                      .preTimestamp(10L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(10L)
                                                      .power(10L)
                                                      .timestamp(10L)
                                                      .preTimestamp(9L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(9L)
                                                      .power(9L)
                                                      .timestamp(9L)
                                                      .preTimestamp(8L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(8L)
                                                      .power(8L)
                                                      .timestamp(8L)
                                                      .preTimestamp(7L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(7L)
                                                      .power(7L)
                                                      .timestamp(7L)
                                                      .preTimestamp(6L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(6L)
                                                      .power(6L)
                                                      .timestamp(6L)
                                                      .preTimestamp(5L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(5L)
                                                      .power(5L)
                                                      .timestamp(5L)
                                                      .preTimestamp(4L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(4L)
                                                      .power(4L)
                                                      .timestamp(4L)
                                                      .preTimestamp(3L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(3L)
                                                      .power(3L)
                                                      .timestamp(3L)
                                                      .preTimestamp(2L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(2L)
                                                      .power(2L)
                                                      .timestamp(2L)
                                                      .preTimestamp(1L)
                                                      .build())
                            .add(SignedMeterDataEntity.builder()
                                                      .energy(1L)
                                                      .power(1L)
                                                      .timestamp(1L)
                                                      .preTimestamp(0L)
                                                      .build())
                            .build();
    }

}
