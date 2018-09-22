package com.humanharvest.organz.controller.components;

import java.time.Duration;

import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;

public class DurationUntilExpiryCell extends TableCell<DonatedOrgan, Duration> {

    private static final DurationFormat format = DurationFormat.X_HOURS_Y_MINUTES_SECONDS;

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
            return;
        }

        DonatedOrgan donatedOrgan = getDonatedOrganForRow();

        if (item == null) { // no expiration
            setText(ExpiryBarUtils.getDurationString(donatedOrgan, format));
            setStyle(null);
            setTextFill(Color.BLACK);

        } else if (ExpiryBarUtils.isDurationZero(item)) {
            // Duration is essentially zero, or is zero, or the organ was overridden
            setText(ExpiryBarUtils.getDurationString(donatedOrgan, format));
            Color darkGrey = Color.rgb(32, 32, 32);
            setBackground(new Background(new BackgroundFill(darkGrey, CornerRadii.EMPTY, Insets.EMPTY)));
            setTextFill(Color.WHITE);

        } else {
            // Progress as a decimal. starts at 0 (at time of death) and goes to 1.
            double progressDecimal = donatedOrgan.getProgressDecimal();
            double fullMarker = donatedOrgan.getFullMarker();

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

            setText(ExpiryBarUtils.getDurationString(donatedOrgan, format));
            setBackground(ExpiryBarUtils.getBackground(progressDecimal, fullMarker));
        }
    }
}
