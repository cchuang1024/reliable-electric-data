package edu.nccu.cs.recorder.domain;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class RocksRepositoryTest {

    @Value("${db.root}")
    private String dbRoot;

    @Test
    public void testGetDbPath(){
        RocksRepository testRepo = new RocksRepository() {
            @Override
            public Path getDbPath() {
                return Paths.get(dbRoot, "testDB");
            }
        };

        Path dbPath = testRepo.getDbPath();

        log.info("db root: {}", dbRoot);
        log.info("db path: {}", dbPath);
    }
}
