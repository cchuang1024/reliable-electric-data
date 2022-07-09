package edu.nccu.cs.dispatchcloud.fixdata;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState;
import org.springframework.data.mongodb.repository.Query;
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

    @Query(value = "{timestamp:{$gte:?0, $lte:?1}}",
            sort = "{timestamp:1}")
    List<FixDataEntity> findByTimestampBetween(Long start, Long end);
}
