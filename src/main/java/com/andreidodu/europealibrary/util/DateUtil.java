package com.andreidodu.europealibrary.util;

import org.springframework.stereotype.Component;

import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class DateUtil {
    public LocalDateTime fileTimeToLocalDateTime(FileTime fileCreationTime) {
        return Instant.ofEpochMilli(fileCreationTime.toMillis())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
