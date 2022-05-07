package edu.nccu.cs.recorder.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Slf4j
public class CleanJob implements Runnable {

    @Autowired
    private SenderStateRepository stateRepository;

    @Override
    public void run() {
        log.warn("remove finished state.");
        List<SenderStateEntity> finishedEntities = stateRepository.findByFinished();
        log.info("found finished state: {}", finishedEntities);
        finishedEntities.forEach(stateRepository::remove);

        log.warn("remove abandon state.");
        List<SenderStateEntity> abandonEntities = stateRepository.findByAbandon();
        log.info("found abandon state: {}", abandonEntities);
        abandonEntities.forEach(stateRepository::remove);
    }
}
