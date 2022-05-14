package edu.nccu.cs.dispatchcloud.verifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VerifierService {

    @Autowired
    private FixDataRepository repository;

    public List<FixDataEntity> getInitFixData() {
        return repository.findByState(FixDataEntity.FixState.INIT.name());
    }


}
