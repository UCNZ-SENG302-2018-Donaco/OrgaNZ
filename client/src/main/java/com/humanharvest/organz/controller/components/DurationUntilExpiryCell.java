package com.humanharvest.organz.controller.components;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.DurationFormatter.Format;

public class DurationUntilExpiryCell extends TableCell<DonatedOrgan, Duration> {

    public DurationUntilExpiryCell(TableColumn<DonatedOrgan, Duration> column) {
        super();
    }

    /**
     * Generates a stylesheet instruction for setting the background colour of a cell.
     * The colour is based on progressForColour, and how much the cell is filled in is based on totalProgress.
     *
     * @param progress how far along the bar should be, from 0 to 1: 0 maps to empty, and 1 maps to full
     * @param fullMarker how far along the lower bound starts; this area will be grey (e.g. 0.9 for near the end)
     * @return stylesheet instruction, in the form "-fx-background-color: linear-gradient(...)"
     */
    private static String getStyleForProgress(double progress, double fullMarker) {
        String green;
        String red;
        String blue = "00"; // no blue

        double lowerPercent;
        double higherPercent;
        double progressForColour;
        String maroonColour = "aa0000";
        String whiteColour = "transparent";
        String greyColour = "aaaaaa";
        String middleColour;

        if (progress < 0.0001) {
            progress = 0;
        }
        double percent = progress * 100;

        // Calculate percentages and the middle colour (white if it's not reached lower bound, maroon if it has)
        if (progress < fullMarker) { // Hasn't reached lower bound yet
            progressForColour = progress / fullMarker;
            lowerPercent = percent;
            higherPercent = fullMarker * 100;
            middleColour = whiteColour;
        } else { // In lower bound
            progressForColour = 1;
            lowerPercent = fullMarker * 100;
            higherPercent = percent;
            middleColour = maroonColour;
        }

        // Calculate colours
        if (progressForColour < 0.5) { // less than halfway, mostly green
            int redNumber = (int) Math.round(progressForColour * 255 * 2);
            red = Integer.toHexString(redNumber);
            if (red.length() == 1) {
                red = "0" + red;
            }
            green = "ff";

        } else { // over halfway, mostly red
            red = "ff";
            int greenNumber = (int) Math.round((1 - progressForColour) * 255 * 2);
            green = Integer.toHexString(greenNumber);
            if (green.length() == 1) {
                green = "0" + green;
            }
        }

        // Check they haven't overflowed
        assert red.length() == 2;
        assert green.length() == 2;

        // Generate style string
        String colour = red + green + blue;
        return String.format("-fx-background-color: "
                        + "linear-gradient(to right, #%s 0%%, #%s %s%%, %s %s%%, %s %s%%, #%s %s%%, #%s 100%%);",
                colour, colour, lowerPercent, middleColour, lowerPercent, middleColour, higherPercent,
                greyColour, higherPercent, greyColour);
    }

    private DonatedOrgan getDonatedOrganForRow() {
        return getTableView().getItems().get(getIndex());
    }

    @Override
    protected void updateItem(Duration item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setStyle(null);

        } else if (item == null) { // no expiration
            Duration timeSinceDeath = Duration.between(
                    getDonatedOrganForRow().getDateTimeOfDonation(),
                    LocalDateTime.now());
            setText("N/A (" + getFormattedDuration(timeSinceDeath, Format.XHoursYMinutesSeconds) + " since death)");
            setStyle(null);
            setTextFill(Color.BLACK);

        } else if (item.isZero() || item.isNegative() || item.equals(Duration.ZERO) ||
                item.minusSeconds(1).isNegative()) {
            // Duration is essentially zero, or is zero, or the organ was overridden
            if (getDonatedOrganForRow().getOverrideReason() == null) {
                Duration timeSinceExpiry = Duration.between(
                        getDonatedOrganForRow().getDateTimeOfDonation()
                                .plus(getDonatedOrganForRow().getOrganType().getMaxExpiration()),
                        LocalDateTime.now());
                setText(String.format("Expired (%s ago)",
                        getFormattedDuration(timeSinceExpiry, Format.XHoursYMinutesSeconds)));
            } else {
                setText("Overridden");
            }
            setStyle("-fx-background-color: #202020");
            setTextFill(Color.WHITE);

        } else {
            String displayedDuration = getFormattedDuration(item, Format.XHoursYMinutesSeconds);

            // Progress as a decimal. starts at 0 (at time of death) and goes to 1.
            double progressDecimal = getDonatedOrganForRow().getProgressDecimal();
            double fullMarker = getDonatedOrganForRow().getFullMarker();

            // Calculate colour
            String style = getStyleForProgress(progressDecimal, fullMarker);

            if (progressDecimal >= fullMarker) {
                setTextFill(Color.WHITE);
                if (isSelected()) {
                    setTextFill(Color.BLACK);
                }
            } else {
                setTextFill(Color.BLACK);
                if (isSelected()) {
                    setTextFill(Color.WHITE);
                }
            }

            setText(displayedDuration);
            setStyle(style);
        }
    }
}
