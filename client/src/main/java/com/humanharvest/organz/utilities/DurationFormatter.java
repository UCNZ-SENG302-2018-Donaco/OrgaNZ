package com.humanharvest.organz.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Abstract class used to format durations.
 */
public abstract class DurationFormatter {

    public enum DurationFormat {
        X_HOURS_Y_MINUTES_SECONDS, BIGGEST, X_HRS_Y_MINS_SECS, DAYS
    }

    /**
     * Returns the duration, formatted to a string.
     *
     * How it is formatted depends on the Format passed in.
     *
     * The options are:
     *
     * X_HOURS_Y_MINUTES_SECONDS: x hours, y minutes (or x hours, y seconds if there are less than 60 seconds).
     * E.g. "5 hours 20 minutes", "102 hours 30 seconds".
     *
     * BIGGEST: the biggest unit of time. E.g. "4 days", "3 hours", "59 seconds".
     *
     * @param duration the duration to format
     * @return the formatted string
     */
    public static String getFormattedDuration(Duration duration, DurationFormat format) {
        switch (format) {
            case X_HOURS_Y_MINUTES_SECONDS:
                return getDurationFormattedXHoursYMinutesSeconds(duration);
            case BIGGEST:
                return getDurationFormattedBiggest(duration);
            case X_HRS_Y_MINS_SECS:
                return getDurationFormattedXHrsYMinsSecs(duration);
            case DAYS:
                return getDurationFormattedDays(duration);
            default:
                throw new UnsupportedOperationException("Unknown format for duration.");
        }
    }

    private static String getDurationFormattedDays(Duration duration) {
        long days = duration.toDays();
        if (days < -1) {
            return days * -1 + " days ago";

        } else if (days == -1) {
            return "1 day ago";

        } else if (days == 0) {
            return "today";

        } else if (days == 1) {
            return "in 1 day";
        } else  {
            return "in " + days + " days";
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
        if (seconds == 1) {
            return "1 second";
        } else {
            return seconds + " seconds";
        }
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
        return getDurationHoursMinsSecsString(duration,
                " hour ", " hours ",
                " second", " seconds",
                " minute", " minutes");

    }

    /**
     * Returns the duration, formatted to display xhrs ymins (or xhrs ysecs if there are less than 60
     * seconds).
     * E.g. "5hrs 20mins", "102hrs 30secs".
     *
     * @param duration the duration to format
     * @return the formatted string
     */
    private static String getDurationFormattedXHrsYMinsSecs(Duration duration) {
        return getDurationHoursMinsSecsString(duration,
                "hr ", "hrs ",
                "sec", "secs",
                "min", "mins");
    }

    private static String getDurationHoursMinsSecsString(Duration duration,
            String singleHour, String multipleHours,
            String oneSecond, String multipleSeconds,
            String oneMinute, String multipleMinutes) {

        String formattedDuration;
        long hours = duration.toHours();
        if (hours == 1) {
            formattedDuration = "1" + singleHour;
        } else {
            formattedDuration = hours + multipleHours;
        }
        long minutes = duration.toMinutes() % 60;
        if (minutes == 0) { // no minutes, just seconds (and perhaps hours)
            long seconds = duration.getSeconds() % 3600;
            if (seconds == 1) {
                formattedDuration += "1" + oneSecond;
            } else {
                formattedDuration += seconds + multipleSeconds;
            }
        } else if (minutes == 1) {
            formattedDuration += "1" + oneMinute;
        } else {
            formattedDuration += minutes + multipleMinutes;
        }
        return formattedDuration;
    }

}
