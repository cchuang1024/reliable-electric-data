package edu.nccu.cs.dispatchcloud.fixdata;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity.FixState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FixDataRepository extends CrudRepository<FixDataEntity, String> {
    List<FixDataEntity> findByState(@Param("state") FixState state);

    Optional<FixDataEntity> findByTimestamp(@Param("timestamp") Long timestamp);

    List<FixDataEntity> findByTimestampInAndState(@Param("timestamp")Set<Long> timestamps, @Param("state") FixState state);
}
