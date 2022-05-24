package edu.nccu.cs.datasender.common;

import edu.nccu.cs.protocol.MeterDataRequest;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.protocol.MeterDataResponse.MessageType;
import edu.nccu.cs.protocol.SignedMeterDataRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class HttpSenderTest {

    @Autowired
    private HttpSender sender;

    @Test
    public void testSendRequest() {
        MeterDataRequest<SignedMeterDataRequest> request = prepareRequest();
        MeterDataResponse response = sender.sendMeterData(request);
        log.info("response: {}", response);
        assertThat(response.getMessage().getType()).isEqualTo(MessageType.SUCCESS);
    }

    private MeterDataRequest<SignedMeterDataRequest> prepareRequest() {
        List<SignedMeterDataRequest> payload =
                List.of(SignedMeterDataRequest.builder()
                                              .power(0L)
                                              .energy(0L)
                                              .signature("")
                                              .timestamp(System.currentTimeMillis())
                                              .preTimestamp(0L)
                                              .build());

        return MeterDataRequest.<SignedMeterDataRequest>builder()
                               .applicationId("app")
                               .token("tok")
                               .edgeTime(System.currentTimeMillis())
                               .payload(payload)
                               .build();
    }

}
