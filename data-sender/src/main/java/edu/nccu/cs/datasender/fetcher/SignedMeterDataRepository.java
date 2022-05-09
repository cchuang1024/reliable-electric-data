package edu.nccu.cs.datasender.fetcher;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignedMeterDataRepository extends PagingAndSortingRepository<SignedMeterDataEntity, String>
{
    List<SignedMeterDataEntity> findByInitialed();
}
