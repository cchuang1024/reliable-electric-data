package edu.nccu.cs.recorder.fetcher;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.recorder.sender.SenderStateEntity;
import edu.nccu.cs.recorder.sender.SenderStateRepository;
import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import static edu.nccu.cs.recorder.sender.SenderStateEntity.RETRY_INIT;
import static edu.nccu.cs.recorder.sender.SenderStateEntity.STATE_INIT;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class SignedMeterJob implements Runnable {

    @Autowired
    private ApplicationContext context;
    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void run() {
        try {
            SignedMeterReader reader = context.getBean(SignedMeterReader.class);
            CompletableFuture<SignedMeterData> compFuture = CompletableFuture.supplyAsync(reader, taskExecutor);
            SignedMeterData meterData = compFuture.get(); log.info("signed meter data: {}", meterData);

            Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
            SignedMeterEntity meter = SignedMeterEntity.getInstanceByInstantAndData(now, meterData);
            log.info("signed meter entity: {}", meter);

            SignedMeterRepository meterRepository = context.getBean(SignedMeterRepository.class);
            meterRepository.save(meter);
            log.info("saved timestamp: {}", meter.getTimestamp());

            SenderStateRepository stateRepository = context.getBean(SenderStateRepository.class);
            SenderStateEntity state =
                    SenderStateEntity.builder()
                                     .timestamp(meter.getTimestamp())
                                     .state(STATE_INIT)
                                     .actionTime(System.currentTimeMillis())
                                     .retry(RETRY_INIT)
                                     .build();
            stateRepository.save(state);
        } catch (InterruptedException | ExecutionException ex) {
            log.error(ExceptionUtils.getStackTrace(ex));
        }

    }
}
