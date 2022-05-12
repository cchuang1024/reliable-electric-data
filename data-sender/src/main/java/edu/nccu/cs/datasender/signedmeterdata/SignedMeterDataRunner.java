package edu.nccu.cs.datasender.signedmeterdata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class SignedMeterDataRunner {

    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Scheduled(cron = "0 * * * * *")
    public void fetchAndSend() {
        SignedMeterDataJob job = context.getBean(SignedMeterDataJob.class);
        taskExecutor.execute(job);
    }

}
