package com.humanharvest.organz.controller.components;

import com.humanharvest.organz.DonatedOrgan;
import javafx.beans.binding.NumberBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ManualOverrideCell extends TableCell<DonatedOrgan, DonatedOrgan> {

    private DonatedOrganEventHandler onOverridePressed;
    private DonatedOrganEventHandler onEditPressed;
    private DonatedOrganEventHandler onCancelPressed;

    public ManualOverrideCell(TableColumn<DonatedOrgan, DonatedOrgan> column,
                              DonatedOrganEventHandler onOverridePressed, DonatedOrganEventHandler onEditPressed,
                              DonatedOrganEventHandler onCancelPressed) {
        this.onOverridePressed = onOverridePressed;
        this.onEditPressed = onEditPressed;
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
                overrideBtn.setDisable(item.getDurationUntilExpiry() != null && item.getDurationUntilExpiry().isZero());
                overrideBtn.setOnAction(event -> onOverridePressed.handle(item));
                overrideBtn.setMaxWidth(Double.POSITIVE_INFINITY);
                HBox.setHgrow(overrideBtn, Priority.ALWAYS);
                interior.getChildren().add(overrideBtn);
            } else {
                // Set cell interior spacing
                interior.setSpacing(5);

                // Setup button container
                VBox buttonBox = new VBox();
                buttonBox.setSpacing(2);
                buttonBox.setMinWidth(60);

                // Create edit button
                Button editBtn = new Button("Edit");
                editBtn.setOnAction(event -> onEditPressed.handle(item));
                editBtn.setMaxWidth(Double.POSITIVE_INFINITY);
                editBtn.setFont(Font.font(12));
                editBtn.setMinHeight(30);

                // Create cancel button
                Button cancelBtn = new Button("Cancel");
                cancelBtn.setOnAction(event -> onCancelPressed.handle(item));
                cancelBtn.setMaxWidth(Double.POSITIVE_INFINITY);
                cancelBtn.setFont(Font.font(12));
                cancelBtn.setMinHeight(30);

                // Create override reason label
                Text reason = new Text(item.getOverrideReason());
                // Bind wrapping width of reason display to width of the column minus width of buttons
                NumberBinding reasonWidth = getTableColumn().widthProperty()
                        .subtract(buttonBox.widthProperty())
                        .subtract(interior.spacingProperty());
                reason.wrappingWidthProperty().bind(reasonWidth);
                setPrefHeight(Control.USE_COMPUTED_SIZE);
                HBox.setHgrow(reason, Priority.ALWAYS);

                // Setup layout of the cell
                buttonBox.getChildren().add(editBtn);
                buttonBox.getChildren().add(cancelBtn);
                interior.getChildren().add(buttonBox);
                interior.getChildren().add(reason);
            }
            setGraphic(interior);
        }
    }

    @FunctionalInterface
    public interface DonatedOrganEventHandler {
        void handle(DonatedOrgan donatedOrgan);
    }
}
