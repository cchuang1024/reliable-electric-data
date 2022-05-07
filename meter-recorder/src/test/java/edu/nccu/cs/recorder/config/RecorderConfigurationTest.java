package edu.nccu.cs.recorder.config;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class RecorderConfigurationTest {

    @Autowired
    private RecorderConfiguration configuration;

    @Test
    public void testContextLoad(){
        Assertions.assertThat(configuration).isNotNull();
        log.info("configuration: {}", configuration);
    }
}
