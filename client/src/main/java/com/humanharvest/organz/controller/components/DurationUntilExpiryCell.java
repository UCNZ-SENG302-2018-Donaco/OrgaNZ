package com.humanharvest.organz.controller.components;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.DurationFormatter.Format;

public class DurationUntilExpiryCell extends TableCell<DonatedOrgan, Duration> {

    public DurationUntilExpiryCell(TableColumn<DonatedOrgan, Duration> column) {
        super();
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
            setBackground(new Background(new BackgroundFill(Color.rgb(32, 32, 32), CornerRadii.EMPTY, Insets.EMPTY)));
            setTextFill(Color.WHITE);

        } else {
            String displayedDuration = getFormattedDuration(item, Format.XHoursYMinutesSeconds);

            // Progress as a decimal. starts at 0 (at time of death) and goes to 1.
            double progressDecimal = getDonatedOrganForRow().getProgressDecimal();
            double fullMarker = getDonatedOrganForRow().getFullMarker();

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
            setBackground(ExpiryBarUtils.getBackground(progressDecimal, fullMarker));
        }
    }
}
