package edu.nccu.cs.recorder.sender;

import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.recorder.fetcher.SignedMeterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_FINISHED;
import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_PENDING;
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
    private HttpSender sender;

    @Override
    public void run() {
        List<SenderStateEntity> initEntities = stateRepository.findByInit();
        log.info("found init: {}", initEntities);

        initEntities.forEach(state -> {
            Optional<SignedMeterEntity> meterEntity = meterRepository.findByTimestamp(state.getTimestamp());
            log.info("init meter entity: {}", meterEntity);

            meterEntity.ifPresent(meter -> {
                state.setActionTime(currentTimeMillis());

                try {
                    sender.send(meter);
                    state.setState(STATE_FINISHED);
                } catch (Exception ex) {
                    log.error(getStackTrace(ex));

                    int retry = state.getRetry() + 1;
                    state.setRetry(retry);
                    state.setState(STATE_PENDING);
                }
            });

            stateRepository.update(state);
        });
    }
}
