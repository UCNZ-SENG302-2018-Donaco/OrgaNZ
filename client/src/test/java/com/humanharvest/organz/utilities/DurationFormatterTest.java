package com.humanharvest.organz.utilities;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;
import static org.junit.Assert.assertEquals;

import java.time.Duration;

import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;

import org.junit.Test;

public class DurationFormatterTest {

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds1() {
        Duration duration = Duration.ofSeconds(0);
        assertEquals("0 hours 0 seconds", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds1a() {
        Duration duration = Duration.ofSeconds(1);
        assertEquals("0 hours 1 second", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds2() {
        Duration duration = Duration.ofSeconds(20);
        assertEquals("0 hours 20 seconds", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds3() {
        Duration duration = Duration.ofSeconds(60);
        assertEquals("0 hours 1 minute", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds4() {
        Duration duration = Duration.ofSeconds(100);
        assertEquals("0 hours 1 minute", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds5() {
        Duration duration = Duration.ofMinutes(3);
        assertEquals("0 hours 3 minutes", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds6() {
        Duration duration = Duration.ofHours(1);
        assertEquals("1 hour 0 seconds", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds7() {
        Duration duration = Duration.ofHours(1).plusMinutes(5);
        assertEquals("1 hour 5 minutes", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds8() {
        Duration duration = Duration.ofHours(3);
        assertEquals("3 hours 0 seconds", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedXHoursYMinutesSeconds9() {
        Duration duration = Duration.ofHours(5000);
        assertEquals("5000 hours 0 seconds", getFormattedDuration(duration, DurationFormat.X_HOURS_Y_MINUTES_SECONDS));
    }

    @Test
    public void getDurationFormattedBiggest1() {
        Duration duration = Duration.ofSeconds(0);
        assertEquals("0 seconds", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest2() {
        Duration duration = Duration.ofSeconds(1);
        assertEquals("1 second", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest3() {
        Duration duration = Duration.ofSeconds(100);
        assertEquals("100 seconds", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest4() {
        Duration duration = Duration.ofSeconds(150);
        assertEquals("2 minutes", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest5() {
        Duration duration = Duration.ofMinutes(100);
        assertEquals("100 minutes", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest6() {
        Duration duration = Duration.ofMinutes(119);
        assertEquals("119 minutes", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest7() {
        Duration duration = Duration.ofHours(3);
        assertEquals("3 hours", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest8() {
        Duration duration = Duration.ofHours(47);
        assertEquals("47 hours", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest9() {
        Duration duration = Duration.ofDays(10);
        assertEquals("10 days", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest10() {
        Duration duration = Duration.ofDays(100);
        assertEquals("3 months", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest11() {
        Duration duration = Duration.ofDays(400);
        assertEquals("13 months", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }

    @Test
    public void getDurationFormattedBiggest12() {
        Duration duration = Duration.ofDays(900);
        assertEquals("2 years", getFormattedDuration(duration, DurationFormat.BIGGEST));
    }
}