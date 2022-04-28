package edu.nccu.cs.datasender.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class StateManagementJob implements Runnable {

    private StateManager stateManager;
    private ApplicationState applicationState;

    @Autowired
    public StateManagementJob(StateManager stateManager,
                              ApplicationState applicationState) {
        this.stateManager = stateManager;
        this.applicationState = applicationState;
    }

    @Override
    public void run() {
        while (true) {
            switch (applicationState.getState()) {
                case INIT:
                    stateManager.connect();
                    break;
                case CONNECTED:
                case DISCONNECTED:
                    stateManager.check();
                    break;
                case DESTROY:
                default:
                    log.warn("zk connection destroy and data sender is going to shutdown.");
                    return;
            }
        }
    }
}
