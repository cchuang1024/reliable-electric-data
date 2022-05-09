package edu.nccu.cs.datasender.fetcher;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    List<SignedMeterDataEntity> findByState(String state);
}
