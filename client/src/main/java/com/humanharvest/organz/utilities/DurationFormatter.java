package com.humanharvest.organz.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Abstract class used to format durations.
 */
public abstract class DurationFormatter {

    /**
     * Returns the duration, formatted to a string.
     *
     * How it is formatted depends on the Format passed in.
     *
     * The options are:
     *
     * XHoursYMinutesSeconds: x hours, y minutes (or x hours, y seconds if there are less than 60 seconds).
     * E.g. "5 hours 20 minutes", "102 hours 30 seconds".
     *
     * Biggest: the biggest unit of time. E.g. "4 days", "3 hours", "59 seconds".
     *
     * @param duration the duration to format
     * @return the formatted string
     */
    public static String getFormattedDuration(Duration duration, Format format) {
        switch (format) {
            case XHoursYMinutesSeconds:
                return getDurationFormattedXHoursYMinutesSeconds(duration);
            case Biggest:
                return getDurationFormattedBiggest(duration);
            default:
                throw new UnsupportedOperationException("Unknown format for duration.");
        }
    }

    private static String getDurationFormattedBiggest(Duration duration) {

        long years = ChronoUnit.YEARS.between(LocalDateTime.now(), LocalDateTime.now().plus(duration));
        if (years > 1) {
            return years + " years";
        }

        long months = ChronoUnit.MONTHS.between(LocalDateTime.now(), LocalDateTime.now().plus(duration));
        if (months > 1) {
            return months + " months";
        }

        long days = duration.toDays();
        if (days > 1) {
            return days + " days";
        }

        long hours = duration.toHours();
        if (hours > 1) {
            return hours + " hours";
        }

        long minutes = duration.toMinutes();
        if (minutes > 1) {
            return minutes + " minutes";
        }

        long seconds = duration.getSeconds();
        return seconds + " seconds";
    }

    /**
     * Returns the duration, formatted to display x hours, y minutes (or x hours, y seconds if there are less than 60
     * seconds).
     * E.g. "5 hours 20 minutes", "102 hours 30 seconds".
     *
     * @param duration the duration to format
     * @return the formatted string
     */
    private static String getDurationFormattedXHoursYMinutesSeconds(Duration duration) {
        String formattedDuration;
        long hours = duration.toHours();
        if (hours == 1) {
            formattedDuration = "1 hour ";
        } else {
            formattedDuration = hours + " hours ";
        }
        long minutes = duration.toMinutes() % 60;
        if (minutes == 0) { // no minutes, just seconds (and perhaps hours)
            long seconds = duration.getSeconds() % 3600;
            if (seconds == 1) {
                formattedDuration += "1 second";
            } else {
                formattedDuration += seconds + " seconds";
            }
        } else if (minutes == 1) {
            formattedDuration += "1 minute";
        } else {
            formattedDuration += minutes + " minutes";
        }
        return formattedDuration;

    }

    public enum Format {
        XHoursYMinutesSeconds, Biggest
    }

}
