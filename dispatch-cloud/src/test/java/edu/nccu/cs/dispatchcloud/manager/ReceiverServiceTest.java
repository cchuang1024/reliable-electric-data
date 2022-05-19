package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class ReceiverServiceTest {

    @Autowired
    private ReceiverService receiverService;
    @Autowired
    private VerifierService verifierService;
    @Autowired
    private SignedMeterDataRepository meterDataRepository;
    @Autowired
    private FixDataRepository fixDataRepository;

    @Test
    public void testLoadContext() {
        assertThat(receiverService).isNotNull();
        assertThat(verifierService).isNotNull();
        assertThat(meterDataRepository).isNotNull();
        assertThat(fixDataRepository).isNotNull();
    }

    private static final String TEST_APPLICATION_ID = "test-01";

    @Test
    public void testBuildInitData() {
        List<SignedMeterDataRequest> payload = prepareCompletePayload();
        List<SignedMeterDataEntity> entities = payload.stream()
                                                      .map(p -> receiverService.buildInitEntity(TEST_APPLICATION_ID, p))
                                                      .collect(Collectors.toList());

        assertThat(entities.size()).isEqualTo(payload.size());
    }

    @Test
    public void testIdempotent() {
        meterDataRepository.deleteAll();

        List<SignedMeterDataRequest> payload = prepareCompletePayload();
        List<SignedMeterDataEntity> entities1 = payload.stream()
                                                       .map(p -> receiverService.buildInitEntity(TEST_APPLICATION_ID, p))
                                                       .collect(Collectors.toList());

        meterDataRepository.saveAll(entities1);

        List<SignedMeterDataEntity> first = StreamSupport.stream(meterDataRepository.findAll().spliterator(), false)
                                                         .collect(Collectors.toList());
        assertThat(first.size()).isEqualTo(entities1.size());

        List<SignedMeterDataEntity> entities2 = payload.stream()
                                                       .map(p -> receiverService.buildInitEntity(TEST_APPLICATION_ID, p))
                                                       .collect(Collectors.toList());

        meterDataRepository.saveAll(entities2);

        List<SignedMeterDataEntity> second = StreamSupport.stream(meterDataRepository.findAll().spliterator(), false)
                                                          .collect(Collectors.toList());

        assertThat(second.size()).isEqualTo(entities2.size())
                                 .isEqualTo(first.size())
                                 .isEqualTo(entities1.size());

        meterDataRepository.deleteAll();
    }

    @Test
    public void testSaveCompleteData() {
        cleanAllData();

        List<SignedMeterDataRequest> payload = prepareCompletePayload();
        List<SignedMeterDataEntity> entities = payload.stream()
                                                      .map(p -> receiverService.buildInitEntity(TEST_APPLICATION_ID, p))
                                                      .collect(Collectors.toList());

        receiverService.saveAll(entities);

        List<FixDataEntity> fixData = verifierService.getInitFixData();

        assertThat(fixData).isEmpty();

        cleanAllData();
    }

    private void cleanAllData() {
        meterDataRepository.deleteAll();
        fixDataRepository.deleteAll();
    }

    private List<SignedMeterDataRequest> prepareCompletePayload() {
        LocalDateTime lastMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusMinutes(1L);

        Instant lastInstant = lastMinute.toInstant(DateTimeUtils.getZoneOffset());
        Instant last2Instant = lastMinute.minusMinutes(1L)
                                         .toInstant(DateTimeUtils.getZoneOffset());
        Instant last3Instant = lastMinute.minusMinutes(2L)
                                         .toInstant(DateTimeUtils.getZoneOffset());

        return List.of(SignedMeterDataRequest.builder()
                                             .timestamp(last3Instant.toEpochMilli())
                                             .preTimestamp(0L)
                                             .power(1L)
                                             .energy(1L)
                                             .signature("")
                                             .build(),
                SignedMeterDataRequest.builder()
                                      .timestamp(last2Instant.toEpochMilli())
                                      .preTimestamp(last3Instant.toEpochMilli())
                                      .power(2L)
                                      .energy(2L)
                                      .signature("")
                                      .build(),
                SignedMeterDataRequest.builder()
                                      .timestamp(lastInstant.toEpochMilli())
                                      .preTimestamp(last2Instant.toEpochMilli())
                                      .power(3L)
                                      .energy(3L)
                                      .signature("")
                                      .build());
    }
}
