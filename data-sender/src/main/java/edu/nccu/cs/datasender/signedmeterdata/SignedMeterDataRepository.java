package edu.nccu.cs.datasender.signedmeterdata;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    List<SignedMeterDataEntity> findByState(@Param("state") String state);

    List<SignedMeterDataEntity> findByTimestampIn(@Param("timestamp") Set<Long> timestamps);
}
