package edu.nccu.cs.datasender.component;

import edu.nccu.cs.datasender.manager.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class ShutdownCleaner implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private StateManager stateManager;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        stateManager.destroy();
    }
}
