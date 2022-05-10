package edu.nccu.cs.datasender.fetcher;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SignedMeterDataFetcher {

    @Autowired
    private SignedMeterDataRepository repository;

    public List<SignedMeterDataEntity> getInitEntities(){
        return repository.findByState(SignedMeterDataEntity.STATE_INIT);
    }

    public List<SignedMeterDataEntity> getPendingEntities(){
        return repository.findByState(SignedMeterDataEntity.STATE_INIT);
    }


}
