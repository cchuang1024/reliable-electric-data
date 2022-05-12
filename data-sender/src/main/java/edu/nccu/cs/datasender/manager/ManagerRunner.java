package edu.nccu.cs.datasender.manager;

import edu.nccu.cs.datasender.manager.StateManagementJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class ManagerRunner implements CommandLineRunner {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @Override
    public void run(String... args) throws Exception {
        StateManagementJob job = context.getBean(StateManagementJob.class);
        log.warn("execute state management job.");
        taskExecutor.execute(job);
    }
}
