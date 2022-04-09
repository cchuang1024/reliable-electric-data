package edu.nccu.cs.recorder.fetcher;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.repository.Cursor;
import org.dizitart.no2.repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static org.dizitart.no2.filters.FluentFilter.where;

@Repository
@Slf4j
public class SignedMeterRepository {

    @Autowired
    @Qualifier("signedMeterDB")
    private Nitrite signedMeterDB;

    private ObjectRepository<SignedMeterEntity> getRepository() {
        return signedMeterDB.getRepository(SignedMeterEntity.class);
    }

    public void save(SignedMeterEntity entity) {
        ObjectRepository<SignedMeterEntity> repository = getRepository();
        repository.insert(entity);
    }

    public Optional<SignedMeterEntity> findByTimestamp(long timestamp) {
        ObjectRepository<SignedMeterEntity> repository = getRepository();
        Cursor<SignedMeterEntity> cursor = repository.find(where("timestamp").eq(timestamp));
        return Optional.ofNullable(cursor.firstOrNull());
    }
}
