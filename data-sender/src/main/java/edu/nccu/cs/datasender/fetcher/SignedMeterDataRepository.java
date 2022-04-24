package edu.nccu.cs.datasender.fetcher;

import org.springframework.data.repository.CrudRepository;

public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
}
