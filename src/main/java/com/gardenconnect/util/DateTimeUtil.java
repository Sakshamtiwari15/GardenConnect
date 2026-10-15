package com.gardenconnect.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Utility for formatting timestamps and calculating relative time labels.
 */
public final class DateTimeUtil {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_PATTERN = "MMM dd, yyyy";
    private static final String DISPLAY_DATETIME_PATTERN = "MMM dd, yyyy HH:mm";

    private DateTimeUtil() {}

    public static String formatDisplayDate(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN).format(timestamp);
    }

    public static String formatDisplayDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATE_PATTERN).format(date);
    }

    public static String formatDisplayDateTime(Timestamp timestamp) {
        if (timestamp == null) return "N/A";
        return new SimpleDateFormat(DISPLAY_DATETIME_PATTERN).format(timestamp);
    }

    public static Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            java.util.Date parsed = new SimpleDateFormat(DATE_PATTERN).parse(dateStr.trim());
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Converts a Timestamp to a friendly relative time description (e.g., "5 mins ago", "2 hours ago", "Yesterday").
     */
    public static String getRelativeTime(Timestamp timestamp) {
        if (timestamp == null) return "Recently";

        Instant then = timestamp.toInstant();
        Instant now = Instant.now();

        long seconds = ChronoUnit.SECONDS.between(then, now);
        if (seconds < 60) {
            return "Just now";
        }

        long minutes = ChronoUnit.MINUTES.between(then, now);
        if (minutes < 60) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        long hours = ChronoUnit.HOURS.between(then, now);
        if (hours < 24) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        long days = ChronoUnit.DAYS.between(then, now);
        if (days == 1) {
            return "Yesterday";
        }
        if (days < 30) {
            return days + " days ago";
        }

        long months = days / 30;
        if (months < 12) {
            return months + (months == 1 ? " month ago" : " months ago");
        }

        long years = days / 365;
        return years + (years == 1 ? " year ago" : " years ago");
    }
}
