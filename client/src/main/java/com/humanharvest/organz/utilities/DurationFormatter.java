package com.humanharvest.organz.utilities;

import java.time.Duration;

/**
 * Abstract class used to format durations.
 */
public abstract class DurationFormatter {

    /**
     * Returns the duration, formatted to display x hours, y minutes (or x hours, y seconds if there are less than 60
     * seconds).
     * E.g. "5 hours 20 minutes", "102 hours 30 seconds".
     *
     * @param duration the duration to format
     * @return the formatted string
     */
    public static String getFormattedDuration(Duration duration) {
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

}
