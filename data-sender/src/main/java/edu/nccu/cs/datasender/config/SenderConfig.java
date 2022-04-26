package edu.nccu.cs.datasender.config;

import edu.nccu.cs.datasender.sender.ApplicationState;
import edu.nccu.cs.datasender.sender.SenderState;
import edu.nccu.cs.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SenderConfig {

    @Value("${db.root}")
    private String dbRoot;

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(8);
        pool.setMaxPoolSize(12);
        pool.setQueueCapacity(100);
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.initialize();
        return pool;
    }

    @Bean
    public ApplicationState applicationState() {
        ApplicationState state = new ApplicationState();
        state.setState(SenderState.INIT);
        state.setToken(TokenGenerator.generateUUIDToken());
        return state;
    }
}
