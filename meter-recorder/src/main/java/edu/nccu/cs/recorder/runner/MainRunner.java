package edu.nccu.cs.recorder.runner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.nccu.cs.recorder.fetcher.SignedMeterJob;
import edu.nccu.cs.recorder.sender.CleanJob;
import edu.nccu.cs.recorder.sender.ReSendJob;
import edu.nccu.cs.recorder.sender.SenderJob;
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

    @Scheduled(cron = "5 * * * * * *")
    public void fetchSignedMeterData() throws ExecutionException, InterruptedException {
        log.warn("fetch signed meter data.");
        runJob(SignedMeterJob.class);
    }

    @Scheduled(cron = "20 * * * * * *")
    public void sendSignedMeterData() throws ExecutionException, InterruptedException {
        log.warn("send signed meter data to collector");
        runJob(SenderJob.class);
    }

    @Scheduled(cron = "35 * * * * * *")
    public void resendSignedMeterData() throws ExecutionException, InterruptedException {
        log.warn("resend signed meter data to collector");
        runJob(ReSendJob.class);
    }

    @Scheduled(cron = "50 * * * * * *")
    public void cleanState() throws ExecutionException, InterruptedException {
        log.warn("clean abandon and finished state");
        runJob(CleanJob.class);
    }

    private <T extends Runnable> void runJob(Class<T> jobClaz) throws ExecutionException, InterruptedException {
        T job = context.getBean(jobClaz);
        CompletableFuture<Void> compFuture = CompletableFuture.runAsync(job, taskExecutor);
        compFuture.get();
    }
}
