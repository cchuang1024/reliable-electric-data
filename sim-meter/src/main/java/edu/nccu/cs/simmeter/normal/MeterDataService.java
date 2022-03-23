package edu.nccu.cs.simmeter.normal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.utils.Randomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static edu.nccu.cs.domain.MeterData.POWER_SCALE;
import static edu.nccu.cs.simmeter.normal.MeterAccumulator.INDEX_ENERGY;
import static edu.nccu.cs.simmeter.normal.MeterAccumulator.INDEX_POWER;
import static edu.nccu.cs.simmeter.normal.MeterDataConfig.BASE_VALUE_SIZE;
import static edu.nccu.cs.simmeter.normal.MeterDataConfig.DIFF_VALUE;
import static edu.nccu.cs.utils.Randomizer.randomDouble;
import static java.lang.System.currentTimeMillis;

@Service
public class MeterDataService {

    @Autowired
    private MeterDataConfig config;
    @Autowired
    private MeterAccumulator accumulator;

    public MeterData getMeterNow() {
        long[] data = accumulator.getCurrent();

        return MeterData.builder()
                        .power(data[INDEX_POWER])
                        .energy(data[INDEX_ENERGY])
                        .build();
    }

    public long getCurrentPower() {
        long timestamp = currentTimeMillis();
        long currentMinute = timestamp / (60 * 1000L);
        int index = (int) currentMinute % BASE_VALUE_SIZE;
        double baseValue = config.getBaseValues().get(index);
        double ratio = randomDouble();

        BigDecimal diffValue = BigDecimal.valueOf(DIFF_VALUE)
                                         .multiply(BigDecimal.valueOf(ratio))
                                         .multiply(Randomizer.randomSign())
                                         .setScale(POWER_SCALE, RoundingMode.HALF_UP);
        BigDecimal powerValue = BigDecimal.valueOf(baseValue)
                                          .add(diffValue)
                                          .multiply(BigDecimal.valueOf(MeterData.POWER_RATIO))
                                          .setScale(0, RoundingMode.FLOOR);

        return powerValue.longValue();
    }
}
