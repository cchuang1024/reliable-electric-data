package edu.nccu.cs.recorder.persistant;

import edu.nccu.cs.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

@Slf4j
public class DbTest {

    static {
        RocksDB.loadLibrary();
    }

    @Test
    public void testDb() {
        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB db = RocksDB.open(options, "./db")) {
                String key1 = "key1";
                String value1 = "value1";
                db.put(key1.getBytes(), value1.getBytes());

                String key2 = "key2";
                final byte[] value2 = db.get(key1.getBytes());
                if (value2 != null) {
                    db.put(key2.getBytes(), value2);
                }

                db.delete(key1.getBytes());
            } catch (RocksDBException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
