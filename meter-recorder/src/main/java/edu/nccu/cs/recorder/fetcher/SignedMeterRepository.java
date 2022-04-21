package edu.nccu.cs.recorder.fetcher;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;

import edu.nccu.cs.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static edu.nccu.cs.recorder.fetcher.LatestTimestampEntity.DEFAULT_ID;
import static org.dizitart.no2.filters.FluentFilter.where;

@Repository
@Slf4j
public class SignedMeterRepository {

    @Autowired
    @Qualifier("signedMeterDB")
    private Nitrite signedMeterDB;

    private Semaphore semaphore;

    public SignedMeterRepository() {
        this.semaphore = new Semaphore(1);
    }

    private ObjectRepository<SignedMeterEntity> getMeterRepository() {
        return signedMeterDB.getRepository(SignedMeterEntity.class);
    }

    private ObjectRepository<LatestTimestampEntity> getTimestampRepository() {
        return signedMeterDB.getRepository(LatestTimestampEntity.class);
    }

    private long getLatestTimestamp() {
        ObjectRepository<LatestTimestampEntity> timestampRepo = getTimestampRepository();
        LatestTimestampEntity entity = timestampRepo.getById(DEFAULT_ID);
        if (Objects.isNull(entity)) {
            LatestTimestampEntity newEntity =
                    LatestTimestampEntity.builder()
                                         .id(DEFAULT_ID)
                                         .timestamp(0L)
                                         .build();
            timestampRepo.insert(newEntity);
            return newEntity.getTimestamp();
        } else {
            return entity.getTimestamp();
        }
    }

    private void updateLatestTimestamp(long latestTimestamp) {
        ObjectRepository<LatestTimestampEntity> timestampRepo = getTimestampRepository();
        LatestTimestampEntity entity = timestampRepo.getById(DEFAULT_ID);
        if (Objects.isNull(entity)) {
            LatestTimestampEntity newEntity =
                    LatestTimestampEntity.builder()
                                         .id(DEFAULT_ID)
                                         .timestamp(latestTimestamp)
                                         .build();
            timestampRepo.insert(newEntity);
        } else {
            entity.setTimestamp(latestTimestamp);
            timestampRepo.update(entity);
        }
    }

    public void save(SignedMeterEntity entity) {
        try {
            semaphore.acquire();
            long latestTimestamp = getLatestTimestamp();
            entity.setPreTimestamp(latestTimestamp);

            ObjectRepository<SignedMeterEntity> meterRepo = getMeterRepository();
            meterRepo.insert(entity);

            updateLatestTimestamp(entity.getTimestamp());
        } catch (InterruptedException e) {
            throw new ApplicationException(e);
        } finally {
            semaphore.release();
        }
    }

    public Optional<SignedMeterEntity> findByTimestamp(long timestamp) {
        ObjectRepository<SignedMeterEntity> repository = getMeterRepository();
        Cursor<SignedMeterEntity> cursor = repository.find(where("timestamp").eq(timestamp));
        return Optional.ofNullable(cursor.firstOrNull());
    }
}
