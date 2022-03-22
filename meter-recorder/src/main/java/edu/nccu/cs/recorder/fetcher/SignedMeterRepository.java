package edu.nccu.cs.recorder.fetcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.nccu.cs.recorder.component.RocksTemplate;
import edu.nccu.cs.recorder.domain.RocksRepository;
import edu.nccu.cs.recorder.util.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import static edu.nccu.cs.recorder.util.DateTimeUtils.isToday;

@Service
@Slf4j
public class SignedMeterRepository implements RocksRepository {

    @Value("${db.root}")
    private String dbRoot;
    @Autowired
    private ApplicationContext context;

    private static final String DB_NAME = "SignedMeterEntities";
    public static final DateTimeFormatter DB_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Path getDbPath() {
        LocalDate date = LocalDate.now();
        String dateStr = date.format(DB_DATE);
        return Paths.get(dbRoot, DB_NAME + dateStr);
    }

    public Path getDbPathByTimestamp(long timestamp) {
        if (isToday(timestamp)) {
            return getDbPath();
        } else {
            LocalDateTime ldt = DateTimeUtils.localDateTimeFromTimestamp(timestamp);
            String dateStr = ldt.format(DB_DATE);
            return Paths.get(dbRoot, DB_NAME + dateStr);
        }
    }

    public long save(SignedMeterEntity entity) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return template.saveTemporalEntity(() -> this.getDbPathByTimestamp(entity.getTimestamp()), entity);
    }

    public Optional<SignedMeterEntity> findByTimestamp(long timestamp) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return template.findTemporalEntity(() -> this.getDbPathByTimestamp(timestamp), timestamp,
                SignedMeterEntity::getInstantFromRawBytes);
    }

    public List<SignedMeterEntity> findByTimestamps(List<Long> timestamps) {
        RocksTemplate template = context.getBean(RocksTemplate.class);
        return timestamps.stream()
                         .map(timestamp -> template.findTemporalEntity(
                                 () -> this.getDbPathByTimestamp(timestamp), timestamp,
                                 SignedMeterEntity::getInstantFromRawBytes))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .collect(Collectors.toList());
    }
}
