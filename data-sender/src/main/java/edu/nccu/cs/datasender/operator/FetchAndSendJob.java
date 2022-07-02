package edu.nccu.cs.datasender.operator;

import edu.nccu.cs.datasender.manager.ApplicationState;
import edu.nccu.cs.datasender.manager.SenderState;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.datasender.signedmeterdata.SignedMeterDataService;
import edu.nccu.cs.protocol.MeterDataResponse;
import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class FetchAndSendJob implements Runnable {

    private final ApplicationState applicationState;
    private final SignedMeterDataService service;

    @Autowired
    public FetchAndSendJob(ApplicationState applicationState, SignedMeterDataService service) {
        this.applicationState = applicationState;
        this.service = service;
    }

    @Override
    public void run() {
        if (applicationState.isStrategyAll()) {
            runFetchAll();
        } else if (applicationState.isStrategeLimited()) {
            runFetchLimitedData();
        } else {
            log.error("unsupported strategy: {}", applicationState.getStrategy());
        }
    }

    private void runFetchAll() {
        log.info("run fetch all data.");
        if (applicationState.getState() == SenderState.CONNECTED) {
            List<SignedMeterDataEntity> sendList = service.collectSendList();
            int sendTimes = service.calculateSendTimes(applicationState.getMaxData(), sendList.size());

            for (int i = 0; i < sendTimes; i++) {
                int start = i * applicationState.getMaxData();
                int boundary = Math.min(((i + 1) * applicationState.getMaxData()), sendList.size());
                List<SignedMeterDataEntity> subEntities = sendList.subList(start, boundary);

                MeterDataResponse response = service.directlySend(applicationState.getId(), applicationState.getToken(), subEntities);
                handleResponse(response, subEntities);
            }
        }
    }

    private void runFetchLimitedData() {
        log.info("run fetch limited data.");
        if (applicationState.getState() == SenderState.CONNECTED) {
            List<SignedMeterDataEntity> sendList = service.collectLimitedSendList(applicationState.getMaxData());
            MeterDataResponse response = service.directlySend(applicationState.getId(), applicationState.getToken(), sendList);
            handleResponse(response, sendList);
        }
    }

    private void handleResponse(MeterDataResponse response, List<SignedMeterDataEntity> sendList) {
        switch (response.getMessage().getType()) {
            case ERROR:
                logError(response);
                return;
            case NOT_AUTHORIZED:
                logNotAuthorized();
                return;
            case FIX:
                logFix(response);
                handlePending(response);
                updateToDone(sendList);
                return;
            case SUCCESS:
            default:
                logSuccess();
                updateToDone(sendList);
        }
    }

    private void updateToDone(List<SignedMeterDataEntity> sendList) {
        List<Long> timestamps = sendList.stream()
                                        .map(SignedMeterDataEntity::getTimestamp)
                                        .collect(Collectors.toList());
        service.updateToDone(timestamps);
    }

    private void handlePending(MeterDataResponse response) {
        List<Long> timestamps = response.getMessage().getPayload();
        service.updateToPending(timestamps);
    }

    private void logFix(MeterDataResponse response) {
        log.warn("have to fix timestamps: {}", response.getMessage().getPayload());
    }

    private void logSuccess() {
        log.warn("send succeeded!");
    }

    private void logError(MeterDataResponse response) {
        log.error("entities send failed: {}\n{}",
                response.getMessage().getMessage(),
                ExceptionUtils.getStackTrace(new Exception(response.getMessage()
                                                                   .getCause())));
    }

    private void logNotAuthorized() {
        log.error("not authorized token: {} of application id {}", applicationState.getToken(), applicationState.getId());
    }
}
