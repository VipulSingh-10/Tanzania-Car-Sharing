package com.singhv.CarSharingTZ.helper;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateHelper {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public String getDateFormatted(int noOfDays) {
        Instant instant = Instant.now().plusSeconds(noOfDays * 24 * 60 * 60);
        return ISO_FORMATTER.format(instant.atZone(ZoneOffset.UTC));
    }

    public static String getDateNowStr() {
        return ISO_FORMATTER.format(Instant.now().atZone(ZoneOffset.UTC));
    }

    public static Date getDateNow() {
        return Date.from(Instant.now());
    }

    public static Date getDateTimeAfterHours(int hours) {
        return Date.from(Instant.now().plusSeconds(hours * 60 * 60));
    }

    public static Date getDateTimeBeforeHours(int hours) {
        return Date.from(Instant.now().minusSeconds(hours * 60 * 60));
    }

    public static Date getDateTimeAfterHours(Date startTime, int hours) {
        Instant instant = startTime.toInstant().plusSeconds(hours * 60 * 60);
        return Date.from(instant);
    }

    public static Date getDateTimeBeforeHours(Date startTime, int hours) {
        Instant instant = startTime.toInstant().minusSeconds(hours * 60 * 60);
        return Date.from(instant);
    }

    public static String formatDate(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Asia/Kolkata"));
        return DISPLAY_FORMATTER.format(localDateTime);
    }
}