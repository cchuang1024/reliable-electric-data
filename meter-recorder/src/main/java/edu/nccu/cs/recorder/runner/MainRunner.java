package edu.nccu.cs.recorder.runner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.nccu.cs.recorder.fetcher.SignedMeterJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class MainRunner {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    // @Scheduled(fixedRate = 60 * 1000L)
    @Scheduled(cron = "10 * * * * * *")
    public void fetchSignedMeterData() throws ExecutionException, InterruptedException {
        log.warn("fetch signed meter data.");

        SignedMeterJob job = context.getBean(SignedMeterJob.class);
        CompletableFuture<Void> compFuture = CompletableFuture.runAsync(job, taskExecutor);
        compFuture.get();
    }
}
