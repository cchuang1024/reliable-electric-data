package edu.nccu.cs.simmeter.normal;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MeterDataConfigTest {

    @Autowired
    private MeterDataConfig config;

    @Test
    public void testLoadConfiguration() {
        Assertions.assertThat(config).isNotNull();
        log.info("config: {}", config);
    }
}
