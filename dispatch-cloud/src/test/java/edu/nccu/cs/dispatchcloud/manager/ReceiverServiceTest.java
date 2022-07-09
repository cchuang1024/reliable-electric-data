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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static edu.nccu.cs.dispatchcloud.manager.ElectricDataController.DEFAULT_EDGE_ID;
import static edu.nccu.cs.utils.DateTimeUtils.getDefault;
import static edu.nccu.cs.utils.DateTimeUtils.getNow;
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

    @Test
    public void testSaveAll(){
        SignedMeterDataEntity entity =
                SignedMeterDataEntity.builder()
                        .edgeId(DEFAULT_EDGE_ID)
                        .timestamp(1657327980000L)
                        .preTimestamp(1657326420000L)
                        .power(98625L)
                        .energy(13739141L)
                        .signature("MEQCIFOopAICZ1Sld1F8nigRURPCiIH8HaIzhL8uDuqS1w+UAiAjQxUqNVOuyDF5hbS7Bb5POX32C8HNUDusTF5s+Nx9iA==")
                        .checkState(SignedMeterDataEntity.CheckState.INIT)
                        .initTime(getNow())
                        .fixTime(getDefault())
                        .doneTime(getDefault())
                        .build()
                        .init();
        receiverService.saveAll(Collections.singletonList(entity));
    }
}
