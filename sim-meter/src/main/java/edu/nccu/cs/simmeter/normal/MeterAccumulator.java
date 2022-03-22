package edu.nccu.cs.simmeter.normal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static edu.nccu.cs.simmeter.normal.MeterData.ENERGY_RATIO;
import static edu.nccu.cs.simmeter.normal.MeterData.POWER_SCALE;
import static edu.nccu.cs.simmeter.normal.MeterData.POWER_UNIT;
import static edu.nccu.cs.simmeter.normal.MeterData.TIME_SCALE;

@Component
@Slf4j
public class MeterAccumulator {

    public static final int INDEX_POWER = 0;
    public static final int INDEX_ENERGY = 1;
    public static final int INDEX_TIMESTAMP = 2;
    private static final Object LOCK = new Object();
    private static final double HOURLY_MILLIS = 3600000.0;
    private AtomicLong power;
    private AtomicLong energy;
    private AtomicLong timestamp;

    public MeterAccumulator() {
        energy = new AtomicLong(0);
        power = new AtomicLong(0);
        timestamp = new AtomicLong(System.currentTimeMillis());
    }

    public synchronized void accumulate(long power, long timestamp) {
        synchronized (LOCK) {
            long deltaEnergy = calculateDeltaEnergy(power, timestamp);

            this.power.set(power);
            this.timestamp.set(timestamp);
            this.energy.addAndGet(deltaEnergy);
        }
    }

    public long calculateDeltaEnergy(long power, long newTimestamp) {
        BigDecimal realPower = BigDecimal.valueOf((double) power)
                                         .multiply(BigDecimal.valueOf(POWER_UNIT))
                                         .setScale(POWER_SCALE, RoundingMode.UNNECESSARY);
        log.info("real power: {}", realPower);

        long preTimestamp = this.timestamp.get();
        long diffTimestamp = newTimestamp - preTimestamp;
        BigDecimal timeRatio = BigDecimal.valueOf((double) diffTimestamp)
                                         .divide(BigDecimal.valueOf(HOURLY_MILLIS), TIME_SCALE, RoundingMode.HALF_UP);
        log.info("time ratio: {}", timeRatio);

        BigDecimal deltaEnergy = realPower.multiply(timeRatio)
                                          .multiply(BigDecimal.valueOf(ENERGY_RATIO));
        log.info("real delta: {}", deltaEnergy);

        // return Double.doubleToLongBits(deltaEnergy.doubleValue());
        return deltaEnergy.longValue();
    }

    public synchronized long[] getCurrent() {
        synchronized (LOCK) {
            return new long[]{
                    power.get(),
                    energy.get(),
                    timestamp.get()
            };
        }
    }
}
