package edu.nccu.cs.datasender.operator;

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

    // @Scheduled(cron = "0 * * * * *")
    public void fetchAndSend() {
        FetchAndSendJob job = context.getBean(FetchAndSendJob.class);
        taskExecutor.execute(job);
    }

}
