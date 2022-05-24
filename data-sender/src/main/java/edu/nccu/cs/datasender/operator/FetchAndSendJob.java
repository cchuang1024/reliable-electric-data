package edu.nccu.cs.datasender.operator;

import edu.nccu.cs.datasender.manager.ApplicationState;
import edu.nccu.cs.datasender.manager.SenderState;
import edu.nccu.cs.datasender.manager.StateManager;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataService;
import edu.nccu.cs.protocol.MeterDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class FetchAndSendJob implements Runnable {

    private StateManager stateManager;
    private ApplicationState applicationState;
    private SignedMeterDataService service;

    @Autowired
    public FetchAndSendJob(StateManager stateManager,
                           ApplicationState applicationState,
                           SignedMeterDataService service) {
        this.stateManager = stateManager;
        this.applicationState = applicationState;
        this.service = service;
    }

    @Override
    public void run() {
        if (applicationState.getState() == SenderState.CONNECTED) {
            List<SignedMeterDataEntity> initEntities = service.getInitEntities();

            MeterDataResponse response = service.sendData(applicationState.getId(), applicationState.getToken(), initEntities);

            // TODO:
            // check if post failed and turn to pending

            // check if response contains fix data and turn to pending

            // query pending

            // resend data

        }

    }
}
