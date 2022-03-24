package edu.nccu.cs.recorder.sender;

import java.util.List;
import java.util.Optional;

import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.recorder.fetcher.SignedMeterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_FINISHED;
import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_PENDING;
import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_WAIT;
import static edu.nccu.cs.utils.ExceptionUtils.getStackTrace;
import static java.lang.System.currentTimeMillis;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SenderJob implements Runnable {

    @Autowired
    private SignedMeterRepository meterRepository;
    @Autowired
    private SenderStateRepository stateRepository;
    @Autowired
    private MqttSender sender;

    @Override
    public void run() {
        List<SenderStateEntity> initEntities = stateRepository.findByInit();

        initEntities.forEach(state -> {
            Optional<SignedMeterEntity> meterEntity = meterRepository.findByTimestamp(state.getTimestamp());
            meterEntity.ifPresent(meter -> {
                try {
                    sender.send(meter);
                    state.getData().setState(STATE_FINISHED);
                } catch (Exception ex) {
                    log.error(getStackTrace(ex));

                    int retry = state.getData().getRetry() + 1;
                    state.getData().setRetry(retry);
                    state.getData().setState(STATE_PENDING);
                }
            });
            state.getData().setActionTime(currentTimeMillis());

            stateRepository.save(state);
        });
    }
}
