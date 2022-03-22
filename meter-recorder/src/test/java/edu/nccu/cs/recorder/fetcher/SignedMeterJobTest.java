package edu.nccu.cs.recorder.fetcher;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootTest
@Slf4j
public class SignedMeterJobTest {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void testRun() throws ExecutionException, InterruptedException {
        log.warn("fetch signed meter data.");

        SignedMeterJob job = context.getBean(SignedMeterJob.class);
        CompletableFuture<Void> compFuture = CompletableFuture.runAsync(job, taskExecutor);
        compFuture.get();
    }

}
