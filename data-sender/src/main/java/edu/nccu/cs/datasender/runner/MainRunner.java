package edu.nccu.cs.datasender.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!test")
public class MainRunner {

    @Autowired
    private ApplicationContext context;

    @Scheduled(fixedRate = 15 * 1000L)
    public void checkState(){

    }

    @Scheduled(cron = "30 * * * * *")
    public void fetchAndSend(){

    }
}
