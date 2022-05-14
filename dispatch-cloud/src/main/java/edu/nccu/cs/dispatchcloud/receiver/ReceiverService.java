package edu.nccu.cs.dispatchcloud.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReceiverService {

    @Autowired
    private SignedMeterDataRepository repository;

    public void save(SignedMeterDataEntity entity) {
        repository.save(entity);
    }

    public void saveAll(List<SignedMeterDataEntity> entities) {
        repository.saveAll(entities);
    }
}
