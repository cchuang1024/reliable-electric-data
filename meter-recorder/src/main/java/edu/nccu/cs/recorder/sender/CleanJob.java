package edu.nccu.cs.recorder.sender;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
        finishedEntities.forEach(stateRepository::remove);

        log.warn("remove abandon state.");
        List<SenderStateEntity> abandonEntities = stateRepository.findByAbandon();
        abandonEntities.forEach(stateRepository::remove);
    }
}
