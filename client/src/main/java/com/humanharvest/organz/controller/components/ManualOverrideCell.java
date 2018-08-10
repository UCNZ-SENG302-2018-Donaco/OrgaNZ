package com.humanharvest.organz.controller.components;

import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import com.humanharvest.organz.DonatedOrgan;

public class ManualOverrideCell extends TableCell<DonatedOrgan, DonatedOrgan> {

    @FunctionalInterface
    public interface DonatedOrganEventHandler {
        void handle(DonatedOrgan donatedOrgan);
    }

    private DonatedOrganEventHandler onOverridePressed;
    private DonatedOrganEventHandler onCancelPressed;

    public ManualOverrideCell(TableColumn<DonatedOrgan, DonatedOrgan> column,
            DonatedOrganEventHandler onOverridePressed, DonatedOrganEventHandler onCancelPressed) {
        this.onOverridePressed = onOverridePressed;
        this.onCancelPressed = onCancelPressed;
    }

    @Override
    protected void updateItem(DonatedOrgan item, boolean empty) {
        super.updateItem(item, empty);
        HBox interior = new HBox();

        if (item == null || empty) {
            setGraphic(null);
        } else {
            if (item.getOverrideReason() == null) {
                // Create override button
                Button overrideBtn = new Button("Override");
                overrideBtn.setOnAction(event -> onOverridePressed.handle(item));
                overrideBtn.setMaxWidth(Double.POSITIVE_INFINITY);
                HBox.setHgrow(overrideBtn, Priority.ALWAYS);
                interior.getChildren().add(overrideBtn);
            } else {
                // Set cell interior spacing
                interior.setSpacing(5);

                // Create cancel button
                Button cancelBtn = new Button("X");
                cancelBtn.setOnAction(event -> onCancelPressed.handle(item));
                cancelBtn.setMinWidth(30);

                // Create override reason label
                Text reason = new Text(item.getOverrideReason());
                // Bind wrapping width of reason display to width of the column minus width of the cancel button
                NumberBinding reasonWidth = getTableColumn().widthProperty()
                        .subtract(cancelBtn.widthProperty())
                        .subtract(interior.spacingProperty());
                reason.wrappingWidthProperty().bind(reasonWidth);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
                HBox.setHgrow(reason, Priority.ALWAYS);

                // Attach both to the interior of the cell
                interior.getChildren().add(cancelBtn);
                interior.getChildren().add(reason);
            }
            setGraphic(interior);
        }
    }
}
