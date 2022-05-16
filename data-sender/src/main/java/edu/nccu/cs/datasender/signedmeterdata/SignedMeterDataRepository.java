package edu.nccu.cs.datasender.signedmeterdata;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    List<SignedMeterDataEntity> findByState(@Param("state") String state);
}