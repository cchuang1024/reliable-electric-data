package edu.nccu.cs.recorder.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.mvstore.MVStoreModule;
import org.dizitart.no2.rocksdb.RocksDBModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RecorderConfig {

    @Value("${db.root}")
    private String dbRoot;

    public static final String SIGNED_METER_NAME = "SignedMeterEntities";
    public static final String SENDER_META_NAME = "SenderMeta";

    @Bean("signedMeterStoreModule")
    public RocksDBModule signedMeterStoreModule() {
        Path dbPath = Paths.get(dbRoot, String.format("%s.db", SIGNED_METER_NAME));
        return RocksDBModule.withConfig()
                            .filePath(dbPath.toString())
                            // .compress(true)
                            .build();
    }

    @Bean("senderMetaStoreModule")
    public RocksDBModule senderMetaStoreModule() {
        Path dbPath = Paths.get(dbRoot, String.format("%s.db", SENDER_META_NAME));
        return RocksDBModule.withConfig()
                            .filePath(dbPath.toString())
                            // .compress(true)
                            .build();
    }

    @Bean("signedMeterDB")
    public Nitrite signedMeterDB(@Qualifier("signedMeterStoreModule") RocksDBModule signedMeterStoreModule) {
        return Nitrite.builder()
                      .loadModule(signedMeterStoreModule)
                      .openOrCreate();
    }

    @Bean("senderMetaDB")
    public Nitrite senderMetaDB(@Qualifier("senderMetaStoreModule") RocksDBModule senderMetaStoreModule) {
        return Nitrite.builder()
                      .loadModule(senderMetaStoreModule)
                      .openOrCreate();
    }

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        return executor;
    }
}
