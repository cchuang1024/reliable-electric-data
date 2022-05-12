package edu.nccu.cs.datasender.signedmeterdata;

import java.util.List;

import edu.nccu.cs.datasender.manager.ApplicationState;
import edu.nccu.cs.datasender.manager.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SignedMeterDataJob implements Runnable {

    private StateManager stateManager;
    private ApplicationState applicationState;
    private SignedMeterDataFetcher fetcher;
    private SignedMeterDataSender sender;

    @Autowired
    public SignedMeterDataJob(StateManager stateManager,
            ApplicationState applicationState,
            SignedMeterDataFetcher fetcher,
            SignedMeterDataSender sender) {
        this.stateManager = stateManager;
        this.applicationState = applicationState;
        this.fetcher = fetcher;
        this.sender = sender;
    }

    @Override
    public void run() {
        List<SignedMeterDataEntity> initEntities = fetcher.getInitEntities();

    }
}
