package edu.nccu.cs.dispatchcloud.manager;

import com.github.zkclient.IZkClient;
import edu.nccu.cs.dispatchcloud.common.ZkClients;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import edu.nccu.cs.protocol.ElectricDataResponse;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static edu.nccu.cs.dispatchcloud.config.Constant.*;
import static edu.nccu.cs.utils.DateTimeUtils.toInstant;
import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SpringBootTest
// @AutoConfigureMockMvc
// @ExtendWith(SpringExtension.class)
// @WebFluxTest(ReceiverController.class)
@Slf4j
public class ElectricDataControllerTest {

    @Autowired
    private ZkClients zkClients;
    @Autowired
    private ElectricDataController controller;
    @Autowired
    private SignedMeterDataRepository meterDataRepository;
    @Autowired
    private FixDataRepository fixDataRepository;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    // @Autowired
    // private MockMvc mockMvc;

    // @Autowired
    // private TestRestTemplate restTemplate;
    // @LocalServerPort
    // private int port;

    private static final String TEST_ID = "test";
    private static final String TEST_TOKEN = "test";

    @Test
    public void testContextLoad() {
        assertThat(controller).isNotNull();
        assertThat(zkClients).isNotNull();
    }

    @Test
    public void testQueryMeterData() throws InterruptedException {
        removeAuthentication();
        removeAllData();

        prepareAuthentication();

        MeterDataRequest<SignedMeterDataRequest> request = prepareRequest();
        controller.receive(request);

        ElectricDataResponse<SignedMeterDataEntity, FixDataEntity> response =
                controller.getElectricData(BASE_DATE);

        assertThat(response.getMeterData()).isNotEmpty();
        assertThat(response.getFixData()).isEmpty();

        log.info("meter data: {}", response.getMeterData());

        removeAuthentication();
        removeAllData();
    }

    @Test
    public void testReceiveMeterData() throws InterruptedException {
        removeAuthentication();
        removeAllData();

        prepareAuthentication();

        MeterDataRequest<SignedMeterDataRequest> request = prepareRequest();
        controller.receive(request);

        List<SignedMeterDataEntity> meterDataEntities = StreamSupport.stream(meterDataRepository.findAll().spliterator(), false)
                                                                     .collect(Collectors.toList());
        log.info("received entities: {}", meterDataEntities);

        assertThat(meterDataEntities).isNotEmpty();

        removeAuthentication();
        removeAllData();
    }

    private void prepareAuthentication() throws InterruptedException {
        try (IZkClient zkClient = zkClients.create()) {
            zkClient.createPersistent(PATH_PARENT, true);

            zkClient.createPersistent(PATH_ID, TEST_ID.getBytes());
            zkClient.createPersistent(PATH_TOKEN, TEST_TOKEN.getBytes());
        }
    }

    private MeterDataRequest<SignedMeterDataRequest> prepareRequest() {
        SignedMeterDataRequest payload =
                SignedMeterDataRequest.builder()
                                      .energy(0L)
                                      .power(0L)
                                      .timestamp(toInstant(BASE_TIME).toEpochMilli())
                                      .preTimestamp(0L)
                                      .signature("")
                                      .build();

        return MeterDataRequest.<SignedMeterDataRequest>builder()
                               .applicationId(TEST_ID)
                               .token(TEST_TOKEN)
                               .edgeTime(System.currentTimeMillis())
                               .payload(List.of(payload))
                               .build();
    }

    private void removeAllData() {
        meterDataRepository.deleteAll();
        fixDataRepository.deleteAll();
    }

    private void removeAuthentication() {
        try (IZkClient zkClient = zkClients.create()) {
            zkClient.delete(PATH_ID);
            zkClient.delete(PATH_TOKEN);
            zkClient.delete(PATH_PARENT);
        }
    }

    @Test
    public void testReceiveAndFix() throws InterruptedException {
        removeAuthentication();
        removeAllData();

        prepareAuthentication();

        MeterDataRequest<SignedMeterDataRequest> request1 = prepareRequest1();
        log.info("request 1 payload: {}", request1.getPayload());
        MeterDataResponse response1 = controller.receive(request1);
        assertThat(response1.getMessage().getType()).isEqualTo(MeterDataResponse.MessageType.SUCCESS);

        TimeUnit.SECONDS.sleep(10L);

        MeterDataRequest<SignedMeterDataRequest> request2 = prepareRequest2();
        log.info("request 2 payload: {}", request2.getPayload());
        MeterDataResponse response2 = controller.receive(request2);
        assertThat(response2.getMessage().getType()).isEqualTo(MeterDataResponse.MessageType.FIX);
        assertThat(response2.getMessage().getPayload().size()).isEqualTo(1);
        assertThat(response2.getMessage().getPayload().get(0)).isEqualTo(BASE_TIME.plusMinutes(1L)
                                                                                  .toInstant(DateTimeUtils.getZoneOffset())
                                                                                  .toEpochMilli());

        TimeUnit.SECONDS.sleep(10L);

        MeterDataRequest<SignedMeterDataRequest> request3 = prepareRequest3(response2.getMessage().getPayload());
        log.info("request 3 payload: {}", request3.getPayload());
        MeterDataResponse response3 = controller.receive(request3);
        assertThat(response3.getMessage().getType()).isEqualTo(MeterDataResponse.MessageType.SUCCESS);

        removeAuthentication();
        removeAllData();
    }

    private static final int TEST_YEAR = 2022;
    private static final int TEST_MONTH = 5;
    private static final int TEST_DATE = 21;
    private static final int TEST_HOUR = 9;
    private static final int TEST_MINUTE = 7;
    private static final String BASE_DATE = String.format("%02d%02d%02d", TEST_YEAR, TEST_MONTH, TEST_DATE);
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(TEST_YEAR, TEST_MONTH, TEST_DATE, TEST_HOUR, TEST_MINUTE, 0);

    @Test
    public void testStringFormat(){
        log.info(String.format("%02d", 5));
    }

    private MeterDataRequest<SignedMeterDataRequest> prepareRequest3(List<Long> payload) {
        Instant baseInstant = toInstant(BASE_TIME);
        Instant missInstant = toInstant(BASE_TIME.plusMinutes(1L));
        Instant currInstant = toInstant(BASE_TIME.plusMinutes(3L));
        Instant preInstant = toInstant(BASE_TIME.plusMinutes(2L));

        SignedMeterDataRequest missPayload =
                SignedMeterDataRequest.builder()
                                      .timestamp(missInstant.toEpochMilli())
                                      .preTimestamp(baseInstant.toEpochMilli())
                                      .power(2L)
                                      .energy(2L)
                                      .signature("")
                                      .build();

        SignedMeterDataRequest currPayload =
                SignedMeterDataRequest.builder()
                                      .timestamp(currInstant.toEpochMilli())
                                      .preTimestamp(preInstant.toEpochMilli())
                                      .power(4L)
                                      .energy(4L)
                                      .signature("")
                                      .build();

        return MeterDataRequest.<SignedMeterDataRequest>builder()
                               .edgeTime(System.currentTimeMillis())
                               .applicationId(TEST_ID)
                               .token(TEST_TOKEN)
                               .payload(List.of(missPayload, currPayload))
                               .build();
    }

    private MeterDataRequest<SignedMeterDataRequest> prepareRequest2() {
        Instant missInstant = toInstant(BASE_TIME.plusMinutes(1L));
        Instant currInstant = toInstant(BASE_TIME.plusMinutes(2L));

        SignedMeterDataRequest payload =
                SignedMeterDataRequest.builder()
                                      .timestamp(currInstant.toEpochMilli())
                                      .preTimestamp(missInstant.toEpochMilli())
                                      .power(3L)
                                      .energy(3L)
                                      .signature("")
                                      .build();

        return MeterDataRequest.<SignedMeterDataRequest>builder()
                               .edgeTime(System.currentTimeMillis())
                               .applicationId(TEST_ID)
                               .token(TEST_TOKEN)
                               .payload(List.of(payload))
                               .build();
    }

    private MeterDataRequest<SignedMeterDataRequest> prepareRequest1() {
        Instant baseInstant = toInstant(BASE_TIME);

        SignedMeterDataRequest payload =
                SignedMeterDataRequest.builder()
                                      .timestamp(baseInstant.toEpochMilli())
                                      .preTimestamp(0L)
                                      .power(1L)
                                      .energy(1L)
                                      .signature("")
                                      .build();

        return MeterDataRequest.<SignedMeterDataRequest>builder()
                               .edgeTime(System.currentTimeMillis())
                               .applicationId(TEST_ID)
                               .token(TEST_TOKEN)
                               .payload(List.of(payload))
                               .build();
    }
}
