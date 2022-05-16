package edu.nccu.cs.dispatchcloud.signedmeterdata;

import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity.CheckState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SignedMeterDataRepository extends CrudRepository<SignedMeterDataEntity, String> {
    Optional<SignedMeterDataEntity> findByTimestamp(@Param("timestamp") Long timestamp);

    Optional<SignedMeterDataEntity> findByPreTimestampAndCheckState(@Param("preTimestamp") Long preTimestamp,
                                                                    @Param("checkState") CheckState checkState);

    List<SignedMeterDataEntity> findByCheckState(@Param("checkState") CheckState checkState);
}
