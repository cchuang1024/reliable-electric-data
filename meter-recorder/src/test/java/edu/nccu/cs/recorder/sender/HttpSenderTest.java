package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class HttpSenderTest {

    @Autowired
    private HttpSender sender;

    @Test
    public void testLoadContext() {
        assertThat(sender).isNotNull();
    }

    @Test
    public void testSendMeter() throws Exception {
        SignedMeterEntity entity =
                SignedMeterEntity.builder()
                                 .energy(0L)
                                 .power(0L)
                                 .timestamp(System.currentTimeMillis())
                                 .preTimestamp(System.currentTimeMillis() - (60 * 1000))
                                 .build();

        sender.send(entity);
    }
}
