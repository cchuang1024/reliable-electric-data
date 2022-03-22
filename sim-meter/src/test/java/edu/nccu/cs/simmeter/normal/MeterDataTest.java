package edu.nccu.cs.simmeter.normal;

import edu.nccu.cs.simmeter.util.DataConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class MeterDataTest {

    @Test
    public void testBuildMeterData() {
        MeterData meterData = MeterData.builder()
                                       .power(1000L)
                                       .energy(10L)
                                       .build();

        assertThat(meterData).hasFieldOrPropertyWithValue("power", 1000L);
        assertThat(meterData).hasFieldOrPropertyWithValue("energy", 10L);

        String result = DataConverter.from(meterData)
                                     .toJson()
                                     .getResult();

        log.info("result: {}", result);
    }
}
