package edu.nccu.cs.simmeter.normal;

import edu.nccu.cs.domain.MeterData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class MeterDataServiceTest {

    @Autowired
    private MeterDataService service;

    @Test
    public void testGetMeterDataNow() {
        MeterData meterData = service.getMeterNow();
        assertThat(meterData).isNotNull();
    }

    @Test
    public void testGetCurrentPower() {
        for (int i = 0; i < 3; i++) {
            long power = service.getCurrentPower();
            assertThat(power).isGreaterThan(0L);
            log.info("power: {}", power);
        }
    }
}
