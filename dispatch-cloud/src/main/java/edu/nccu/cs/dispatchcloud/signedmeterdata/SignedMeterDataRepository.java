package edu.nccu.cs.dispatchcloud.signedmeterdata;

import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    Optional<SignedMeterDataEntity> findByTimestamp(Long timestamp);

    @Query(value = "{timestamp:{$gte:?0, $lte:?1}}",
            sort = "{timestamp:1}")
    List<SignedMeterDataEntity> findByTimestampBetween(Long start, Long end);

    @Query(value = "{preTimestamp:{$gte:?0, $lte:?1}}",
            sort = "{timestamp:1}")
    List<SignedMeterDataEntity> findByPreTimestampBetween(Long start, Long end);

    List<SignedMeterDataEntity> findByPreTimestampIn(Set<Long> preTimestamps);

    Optional<SignedMeterDataEntity> findByPreTimestampAndCheckState(Long preTimestamp, CheckState checkState);

    List<SignedMeterDataEntity> findByCheckState(CheckState checkState);
}
