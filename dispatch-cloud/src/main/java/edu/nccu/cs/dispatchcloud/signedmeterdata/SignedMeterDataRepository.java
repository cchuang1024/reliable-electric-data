package edu.nccu.cs.dispatchcloud.signedmeterdata;

import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    Optional<SignedMeterDataEntity> findByTimestamp(Long timestamp);

    List<SignedMeterDataEntity> findByTimestampBetween(Long start, Long end);

    Optional<SignedMeterDataEntity> findByPreTimestampAndCheckState(Long preTimestamp, CheckState checkState);

    List<SignedMeterDataEntity> findByCheckState(CheckState checkState);
}
