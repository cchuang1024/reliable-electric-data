package edu.nccu.cs.simmeter.normal;

import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Slf4j
public class MeterCollectJob {

    @Autowired
    private MeterDataService service;
    @Autowired
    private MeterAccumulator accumulator;

    private final AtomicLong counter;

    public MeterCollectJob() {
        this.counter = new AtomicLong(0L);
    }

    @Scheduled(fixedRate = 60 * 1000L)
    public void collect() {
        long currCounter = counter.getAndIncrement();

        if (currCounter == 0L) {
            return;
        }

        long timestamp = System.currentTimeMillis();
        long power = service.getCurrentPower();
        accumulator.accumulate(power, timestamp);

        log.warn("run collecting {} times at {}", currCounter, timestamp);
    }
}
