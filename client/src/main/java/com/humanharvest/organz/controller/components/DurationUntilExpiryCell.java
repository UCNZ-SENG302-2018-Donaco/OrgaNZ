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
import com.humanharvest.organz.DonatedOrgan.OrganState;
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

        setText(ExpiryBarUtils.getDurationString(donatedOrgan, format));

        OrganState organState = donatedOrgan.getState();
        switch (organState) {
            case OVERRIDDEN:
            case EXPIRED:
                Color darkGrey = Color.rgb(32, 32, 32);
                setBackground(new Background(new BackgroundFill(darkGrey, CornerRadii.EMPTY, Insets.EMPTY)));
                setTextFill(Color.WHITE);
                break;
            case NO_EXPIRY:
                setStyle(null);
                setTextFill(Color.BLACK);
                break;
            case CURRENT:
                double progressDecimal = donatedOrgan.getProgressDecimal();
                double fullMarker = donatedOrgan.getFullMarker();
                // Use white text if the progress has gone past the marker, otherwise black
                boolean whiteText = progressDecimal >= fullMarker;
                // Invert the text colour if it is selected
                if (isSelected()) {
                    whiteText = !whiteText;
                }
                setTextFill(whiteText ? Color.WHITE : Color.BLACK);
                setBackground(ExpiryBarUtils.getBackground(progressDecimal, fullMarker));
                break;
        }
    }
}
