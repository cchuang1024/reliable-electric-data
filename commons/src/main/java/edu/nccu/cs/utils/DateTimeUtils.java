package edu.nccu.cs.utils;

import java.sql.Timestamp;
import java.time.*;

public class DateTimeUtils {

    public static boolean isToday(long timestamp) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atTime(0, 0, 0);
        LocalDateTime end = start.plusDays(1);

        return timestampFromLocalDateTime(start) <= timestamp
                && timestamp < timestampFromLocalDateTime(end);
    }

    public static ZoneOffset getZoneOffset() {
        return ZoneOffset.ofHours(8);
    }

    public static ZoneId getZoneId() {
        return ZoneId.of("UTF+8");
    }

    public static long timestampFromLocalDateTime(LocalDateTime ldt) {
        return ldt.toInstant(getZoneOffset()).toEpochMilli();
    }

    public static LocalDateTime localDateTimeFromTimestamp(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), getZoneId());
    }

    public static Timestamp getNow() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static Timestamp getDefault() {
        return new Timestamp(0L);
    }

}
