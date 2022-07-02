package edu.nccu.cs.dispatchcloud.fixdata;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FixDataRepository extends CrudRepository<FixDataEntity, String> {
    List<FixDataEntity> findByState(FixState state);

    List<FixDataEntity> findByStateAndTimestampBefore(FixState state, Long timestamp);

    Optional<FixDataEntity> findByTimestamp(Long timestamp);

    List<FixDataEntity> findByTimestampInAndState(Set<Long> timestamps, FixState state);

    List<FixDataEntity> findByTimestampBetween(Long start, Long end);
}
