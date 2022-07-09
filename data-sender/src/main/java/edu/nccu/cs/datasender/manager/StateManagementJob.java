package edu.nccu.cs.datasender.manager;

import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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

    public static final long INIT_WAIT_PERIOD = 10L;
    public static final long CONNECT_WAIT_PERIOD = 30L;

    public static final long DISCONNECT_WAIT_PERIOD = 10L;

    @Override
    public void run() {
        try {
            while (true) {
                switch (applicationState.getState()) {
                    case INIT:
                        log.warn("INIT state, connect to zk.");
                        stateManager.connect();
                        TimeUnit.SECONDS.sleep(INIT_WAIT_PERIOD);
                        break;
                    case CONNECTED:
                        log.warn("CONNECTED state, check connection.");
                        stateManager.check();
                        TimeUnit.SECONDS.sleep(CONNECT_WAIT_PERIOD);
                        break;
                    case DISCONNECTED:
                        log.warn("DISCONNECTED state, check connection.");
                        stateManager.connect();
                        TimeUnit.SECONDS.sleep(DISCONNECT_WAIT_PERIOD);
                        break;
                    case DESTROY:
                    default:
                        log.warn("DESTROY state, zk connection destroy and data sender is going to shutdown.");
                        return;
                }
            }
        } catch (InterruptedException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }
    }
}
