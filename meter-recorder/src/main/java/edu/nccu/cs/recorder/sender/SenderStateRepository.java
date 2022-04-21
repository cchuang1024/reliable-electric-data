package edu.nccu.cs.recorder.sender;

import java.util.List;

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
public class SenderStateRepository {

    @Autowired
    @Qualifier("senderMetaDB")
    private Nitrite senderMetaDB;

    private ObjectRepository<SenderStateEntity> getRepository() {
        return senderMetaDB.getRepository(SenderStateEntity.class);
    }

    public void save(SenderStateEntity entity) {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        repository.insert(entity);
    }

    public List<SenderStateEntity> findByInit() {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        Cursor<SenderStateEntity> cursor = repository.find(where("state").eq(SenderStateEntity.STATE_INIT));
        return cursor.toList();
    }

    public List<SenderStateEntity> findByPending() {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        Cursor<SenderStateEntity> cursor = repository.find(where("state").eq(SenderStateEntity.STATE_PENDING));
        return cursor.toList();
    }

    public List<SenderStateEntity> findByFinished() {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        Cursor<SenderStateEntity> cursor = repository.find(where("state").eq(SenderStateEntity.STATE_FINISHED));
        return cursor.toList();
    }

    public List<SenderStateEntity> findByAbandon() {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        Cursor<SenderStateEntity> cursor = repository.find(where("state").eq(SenderStateEntity.STATE_ABANDON));
        return cursor.toList();
    }

    public void remove(SenderStateEntity entity) {
        ObjectRepository<SenderStateEntity> repository = getRepository();
        repository.remove(entity);
    }
}
