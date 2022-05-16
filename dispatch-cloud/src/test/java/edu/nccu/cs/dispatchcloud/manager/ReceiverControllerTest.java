package edu.nccu.cs.dispatchcloud.manager;

import com.github.zkclient.IZkClient;
import edu.nccu.cs.dispatchcloud.common.ZkClients;
import edu.nccu.cs.dispatchcloud.manager.ReceiverController;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static edu.nccu.cs.dispatchcloud.config.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SpringBootTest
// @AutoConfigureMockMvc
// @ExtendWith(SpringExtension.class)
// @WebFluxTest(ReceiverController.class)
@Slf4j
public class ReceiverControllerTest {

    @Autowired
    private ZkClients zkClients;
    @Autowired
    private ReceiverController controller;
    @Autowired
    private SignedMeterDataRepository meterDataRepository;

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
                                      .timestamp(System.currentTimeMillis())
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
    }

    private void removeAuthentication() {
        try (IZkClient zkClient = zkClients.create()) {
            zkClient.delete(PATH_ID);
            zkClient.delete(PATH_TOKEN);
            zkClient.delete(PATH_PARENT);
        }
    }
}
