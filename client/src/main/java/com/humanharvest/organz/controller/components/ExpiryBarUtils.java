package com.humanharvest.organz.controller.components;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;

public final class ExpiryBarUtils {

    public static final Color greyColour = Color.rgb(170, 170, 170);
    public static final Color darkGreyColour = Color.rgb(100, 100, 100);
    public static final Color noExpiryGreenColour = Color.rgb(191, 255, 164);
    private static final Color maroonColour = Color.rgb(170, 0, 0);

    private ExpiryBarUtils() {
    }

    /**
     * Generates a stylesheet instruction for setting the background colour of a cell.
     *
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param lowerBound how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @return A list of stops that will result in the correct display of the progress bar
     */
    public static List<Stop> getStops(double progress, double lowerBound) {

        if (progress < 0.0001) {
            progress = 0;
        }

        if (progress < 0.0001) {
            progress = 0;
        }

        // Calculate percentages and the middle colour (white if it's not reached lower bound, maroon if it has)
        double lowerPercent;
        double higherPercent;
        double progressForColour;
        Color middleColour;
        if (progress < lowerBound) { // Hasn't reached lower bound yet
            progressForColour = progress / lowerBound;
            lowerPercent = progress;
            higherPercent = lowerBound;
            middleColour = Color.WHITE;
        } else { // In lower bound
            progressForColour = 1;
            lowerPercent = lowerBound;
            higherPercent = progress;
            middleColour = maroonColour;
        }

        Color primaryColour;
        // Calculate colours
        if (progressForColour < 0.5) { // less than halfway, mostly green
            double redValue = progressForColour * 2;
            primaryColour = Color.color(redValue, 1, 0);
        } else { // over halfway, mostly red
            double greenValue = (1 - progressForColour) * 2;
            primaryColour = Color.color(1, greenValue, 0);
        }

        List<Stop> stops = new ArrayList<>();
        stops.add(new Stop(0, primaryColour));
        stops.add(new Stop(lowerPercent, primaryColour));
        stops.add(new Stop(lowerPercent, middleColour));
        stops.add(new Stop(higherPercent, middleColour));
        stops.add(new Stop(higherPercent, greyColour));
        stops.add(new Stop(1, greyColour));

        return stops;
    }

    /**
     * Get a LinearGradient from the progress and lowerBound, will be proportional
     *
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param lowerBound how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @return the proportional LinearGradient object
     */
    public static LinearGradient getLinearGradient(double progress, double lowerBound) {
        return new LinearGradient(0, 0.5, 1, 0.5,
                true, CycleMethod.NO_CYCLE, getStops(progress, lowerBound));
    }

    /**
     * Get a LinearGradient from the progress and lowerBound with specific relative coords as given
     *
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param lowerBound how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @param startX The starting x position
     * @param startY The starting y position
     * @param endX The ending x position
     * @param endY The ending y position
     * @return the relative LinearGradient object
     */
    public static LinearGradient getLinearGradient(double progress, double lowerBound,
            double startX, double startY, double endX, double endY) {

        return new LinearGradient(startX, startY, endX, endY,
                false, CycleMethod.NO_CYCLE, getStops(progress, lowerBound));
    }

    /**
     * Generate a background object from a given progress and lowerBound that will represent the progress bar when
     * applied
     *
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param lowerBound how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @return A background object that can be applied to any Region
     */
    public static Background getBackground(double progress, double lowerBound) {
        LinearGradient linearGradient = getLinearGradient(progress, lowerBound);

        BackgroundFill backgroundFill = new BackgroundFill(linearGradient, CornerRadii.EMPTY, Insets.EMPTY);

        return new Background(backgroundFill);
    }

    public static String getDurationString(DonatedOrgan donatedOrgan, DurationFormat format) {
        Duration durationUntilExpiry = donatedOrgan.getDurationUntilExpiry();
        OrganState organState = donatedOrgan.getState();
        switch (organState) {
            case OVERRIDDEN:
                return "Overridden";
            case NO_EXPIRY:
                Duration timeSinceDeath = Duration.between(donatedOrgan.getDateTimeOfDonation(), LocalDateTime.now());
                return String.format("No expiry (%s since death)", getFormattedDuration(timeSinceDeath, format));
            case EXPIRED:
                Duration timeSinceExpiry = Duration.between(
                        donatedOrgan.getDateTimeOfDonation()
                                .plus(donatedOrgan.getOrganType().getMaxExpiration()), LocalDateTime.now());
                return String.format("Expired (%s ago)", getFormattedDuration(timeSinceExpiry, format));
            case CURRENT:
                return getFormattedDuration(durationUntilExpiry, format);
            default:
                return "Could not calculate duration";
        }
    }

    public static boolean isDurationZero(Duration duration) {
        return duration == null ||
                duration.isZero() ||
                duration.isNegative() ||
                duration.equals(Duration.ZERO) ||
                duration.minusSeconds(1).isNegative();
    }
}

