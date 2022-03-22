package edu.nccu.cs.simmeter.normal;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static edu.nccu.cs.simmeter.normal.MeterAccumulator.INDEX_TIMESTAMP;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class MeterDataAccumulatorTest {

    @Autowired
    private MeterAccumulator accumulator;

    @Test
    public void testGetCurrent() {
        long[] currentValue = accumulator.getCurrent();

        assertThat(currentValue).hasSize(3);

        log.info("current value: {}", currentValue);
    }

    @Test
    public void testCalculateDeltaEnergy() throws InterruptedException {
        long[] origValues = accumulator.getCurrent();

        for (int i = 0; i < 12; i++) {
            TimeUnit.SECONDS.sleep(10L);
            long newTimestamp = System.currentTimeMillis();
            long deltaEnergy = accumulator.calculateDeltaEnergy(1000L, newTimestamp);

            log.info("orig timestamp: {}", origValues[INDEX_TIMESTAMP]);
            log.info("new timestamp: {}", newTimestamp);
            log.info("delta timestamp: {}", (newTimestamp - origValues[INDEX_TIMESTAMP]));
            log.info("active power: {}", 1000L);
            log.info("delta energy: {}", deltaEnergy);
        }
    }

    @Test
    public void testAccumulate() throws InterruptedException {
        long[] initValue = accumulator.getCurrent();

        for (int i = 0; i < 12; i++) {
            TimeUnit.SECONDS.sleep(10L);
            long newTimestamp = System.currentTimeMillis();
            accumulator.accumulate(1000L, newTimestamp);
            long[] nextValue = accumulator.getCurrent();
            assertThat(nextValue).isNotEqualTo(initValue);
            log.info("next value: {}", nextValue);
        }
    }
}
