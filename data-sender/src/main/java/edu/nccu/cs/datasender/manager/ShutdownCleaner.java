package edu.nccu.cs.datasender.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShutdownCleaner implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private StateManager stateManager;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.warn("data sender shut down by user, destroy zk connection.");
        stateManager.destroy();
    }
}
