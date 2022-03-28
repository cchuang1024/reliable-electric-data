package edu.nccu.cs.recorder.sender;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

@Slf4j
public class ReactiveSenderTest {

    private final LinkedBlockingQueue<Long> tsQueue = new LinkedBlockingQueue<>();

    @Test
    public void testReactiveSenderNoBlocking() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(tsPublisher2());

        TimeUnit.SECONDS.sleep(120L);
    }

    public Runnable tsPublisher2() {
        return ()->{
            Flux.interval(Duration.ofSeconds(10))
                .doOnNext(sec -> tsQueue.offer(System.currentTimeMillis()))
                .subscribe(sec -> {
                    Long ts = tsQueue.poll();
                    if (Objects.isNull(ts)) {
                        log.info("ts is null");
                    } else {
                        log.info("get ts: {}", ts);
                    }
                });
        };
    }

    @Test
    public void testReactiveSender() throws InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.execute(tsPublisher());
        exec.execute(tsSender());

        TimeUnit.SECONDS.sleep(120L);
    }

    public Runnable tsPublisher() {
        return () -> {
            try {
                for (int i = 0; i < 10; i++) {
                    tsQueue.offer(System.currentTimeMillis());
                    TimeUnit.SECONDS.sleep(10L);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        };
    }

    public Runnable tsSender() {
        return () -> {
            Flux.<Long>generate(sink -> {
                /*
                Long ts = tsQueue.poll();
                if (Objects.isNull(ts)) {
                    sink.complete();
                } else {
                    sink.next(ts);
                }
                 */
                try {
                    Long ts = tsQueue.take();
                    sink.next(ts);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }).subscribe(ts -> log.info("get timestamp: {}", ts));
        };
    }
}
