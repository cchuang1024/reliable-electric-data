package edu.nccu.cs.dispatchcloud.manager;

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
public class MainRunner {
    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Scheduled(fixedRate = 3 * 60 * 1000L)
    public void fetchAndSend() {
        SelfVerifierJob job = context.getBean(SelfVerifierJob.class);
        taskExecutor.execute(job);
    }

}
