package edu.nccu.cs.recorder.sender;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.nccu.cs.recorder.component.RocksTemplate;
import edu.nccu.cs.recorder.domain.RocksIteratorWrapper;
import edu.nccu.cs.recorder.domain.RocksRepository;
import edu.nccu.cs.recorder.exception.ApplicationException;
import edu.nccu.cs.recorder.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SenderStateRepository implements RocksRepository {

    @Value("${db.root}")
    private String dbRoot;
    @Autowired
    private ApplicationContext context;

    @Override
    public Path getDbPath() {
        return Paths.get(dbRoot, "SenderMeta");
    }

    public long save(SenderStateEntity entity) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return template.saveTemporalEntity(this::getDbPath, entity);
    }

    public Optional<SenderStateEntity> findByTimestamp(long timestamp) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return template.findTemporalEntity(this::getDbPath, timestamp, SenderStateEntity::getInstantFromRawBytes);
    }

    public List<SenderStateEntity> findByTimestamps(List<Long> timestamps) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return timestamps.stream()
                         .map(timestamp ->
                                 template.findTemporalEntity(this::getDbPath, timestamp, SenderStateEntity::getInstantFromRawBytes))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toList());
    }

    public List<SenderStateEntity> findByInit() {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, getDbPath().toString());
                    final RocksIteratorWrapper wrapper = new RocksIteratorWrapper(db.newIterator())) {

                return null;
            } catch (RocksDBException | IOException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                throw new ApplicationException(e);
            }
        }
    }
}
