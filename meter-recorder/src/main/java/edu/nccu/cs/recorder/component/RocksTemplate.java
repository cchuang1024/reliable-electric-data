package edu.nccu.cs.recorder.component;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import edu.nccu.cs.recorder.domain.TemporalKeyValueEntity;
import edu.nccu.cs.recorder.exception.ApplicationException;
import edu.nccu.cs.recorder.exception.SystemException;
import edu.nccu.cs.recorder.util.ByteUtils;
import edu.nccu.cs.recorder.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Component
@Slf4j
@Scope(SCOPE_PROTOTYPE)
public class RocksTemplate {

    public long saveTemporalEntity(Supplier<Path> dbPathSupplier, TemporalKeyValueEntity entity) {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, dbPathSupplier.get().toString())) {
                db.put(entity.getKey(), entity.getValue());
                return entity.getTimestamp();
            } catch (RocksDBException | SystemException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                throw new ApplicationException(e);
            }
        }
    }

    public <R> Optional<R> findTemporalEntity(Supplier<Path> dbPathSupplier, long timestamp, BiFunction<byte[], byte[], R> builder) {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, dbPathSupplier.get().toString())) {
                byte[] key = ByteUtils.getBytesFromLong(timestamp);
                byte[] value = db.get(key);

                if (Arrays.isNullOrEmpty(value)) {
                    return Optional.empty();
                }

                return Optional.ofNullable(builder.apply(key, value));
            } catch (RocksDBException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                throw new ApplicationException(e);
            }
        }
    }

    public long update(Supplier<Path> dbPathSupplier, long timestamp, TemporalKeyValueEntity entity) {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, dbPathSupplier.get().toString())) {
                byte[] key = ByteUtils.getBytesFromLong(timestamp);
                byte[] value = entity.getValue();

                db.put(key, value);

                return timestamp;
            } catch (RocksDBException | SystemException e) {
                log.error(ExceptionUtils.getStackTrace(e));
                throw new ApplicationException(e);
            }
        }
    }
}
