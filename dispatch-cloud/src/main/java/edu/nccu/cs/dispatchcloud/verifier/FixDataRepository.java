package edu.nccu.cs.dispatchcloud.verifier;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FixDataRepository extends CrudRepository<FixDataEntity, String> {
    List<FixDataEntity> findByState(@Param("state") String state);
}
