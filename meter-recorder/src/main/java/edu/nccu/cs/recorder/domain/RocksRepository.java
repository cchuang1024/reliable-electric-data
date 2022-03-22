package edu.nccu.cs.recorder.domain;

import java.nio.file.Path;

public interface RocksRepository {
    Path getDbPath();
}
