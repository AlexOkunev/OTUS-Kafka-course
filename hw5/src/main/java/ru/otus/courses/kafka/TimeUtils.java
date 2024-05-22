package ru.otus.courses.kafka;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class TimeUtils {
    public static LocalTime toLocalTime(Long epochMillis) {
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.systemDefault()).toLocalTime();
    }
}
