package edu.nccu.cs.datasender.manager;

import edu.nccu.cs.datasender.common.ZkService;
import edu.nccu.cs.exception.ApplicationException;
import edu.nccu.cs.utils.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StateManager {

    @Autowired
    private ZkService zkService;
    @Autowired
    private ApplicationState applicationState;

    public ApplicationState connect() {
        String token = TokenGenerator.generateUUIDToken();
        String id = applicationState.getId();

        try {
            zkService.createNode(token, id);
            applicationState.setToken(token);
            applicationState.setState(SenderState.CONNECTED);
        } catch (ApplicationException ex) {
            applicationState.setToken(token);
            applicationState.setState(SenderState.INIT);
        }

        return applicationState;
    }

    public ApplicationState check() {
        if (!zkService.isConnected()) {
            applicationState.setState(SenderState.DISCONNECTED);
        }

        return applicationState;
    }

    public ApplicationState destroy() {
        if (zkService.isConnected()) {
            zkService.destroy();
        } else {
            zkService.close();
        }

        applicationState.setState(SenderState.DESTROY);

        return applicationState;
    }
}
