package edu.nccu.cs.datasender.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class StateCheckJob implements Runnable {

    private StateManager stateManager;
    private ApplicationState applicationState;

    @Autowired
    public StateCheckJob(StateManager stateManager,
            ApplicationState applicationState) {
        this.stateManager = stateManager;
        this.applicationState = applicationState;
    }

    @Override
    public void run() {
        switch (applicationState.getState()) {
            case INIT:
                log.warn("INIT state, connect to zk.");
                stateManager.connect();
                break;
            case CONNECTED:
                log.warn("CONNECTED state, check connection.");
                stateManager.check();
                break;
            case DISCONNECTED:
                log.warn("DISCONNECTED state, check connection.");
                stateManager.check();
                break;
            case DESTROY:
            default:
                log.warn("DESTROY state, zk connection destroy and data sender is going to shutdown.");
                return;
        }
    }
}
