package edu.nccu.cs.datasender.manager;

import edu.nccu.cs.datasender.common.ZkService;
import edu.nccu.cs.exception.ApplicationException;
import edu.nccu.cs.utils.TokenGenerator;
import edu.nccu.cs.utils.TypedPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
            checkExpiredToken(token);

            zkService.createNode(token, id);

            applicationState.setToken(token);
            applicationState.setState(SenderState.CONNECTED);
        } catch (ApplicationException ex) {
            applicationState.setToken(token);
            applicationState.setState(SenderState.INIT);
        }

        return applicationState;
    }

    private void checkExpiredToken(String myToken) {
        String myId = applicationState.getId();

        if (zkService.isConnected() &&
                zkService.isIdExists()) {
            TypedPair<String> idToken = zkService.getIdAndToken();

            String zkId = idToken.left();
            String zkToken = idToken.right();

            if (Objects.equals(zkId, myId)
                    && !Objects.equals(zkToken, myToken)) {
                zkService.deleteTokenNode();
                zkService.deleteIdNode();
            }
        }
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
