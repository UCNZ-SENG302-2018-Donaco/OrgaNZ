package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.skin.ToggleButtonSkin;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ToggleButtonSkinAndroid extends ToggleButtonSkin {
    private final StackPane toggleStatus;
    private final StackPane toggleLight;

    public ToggleButtonSkinAndroid(ToggleButton toggleButton) {
        super(toggleButton);
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> ToggleButtonSkinAndroid.this.getSkinnable().arm());
        this.toggleStatus = new StackPane();
        this.toggleStatus.getStyleClass().setAll("toggle-status");
        this.toggleLight = new StackPane();
        this.toggleLight.getStyleClass().setAll("toggle-light");
        this.toggleStatus.getChildren().clear();
        this.toggleStatus.getChildren().add(this.toggleLight);
        this.getChildren().add(this.toggleStatus);
    }

    private static String getWebColor(Color color) {
        int red = (int) (color.getRed() * 255.0D);
        int green = (int) (color.getGreen() * 255.0D);
        int blue = (int) (color.getBlue() * 255.0D);
        return "#" + String.format("%02X", red) + String.format("%02X", green) + String.format("%02X", blue);
    }

    public void setLightColor(Color color) {
        this.toggleLight.setStyle("-fx-background-color:" + getWebColor(color));
    }

    protected void layoutChildren(double x, double y, double w, double h) {
        //TODO: Check if width and height are around the wrong way here
        super.layoutChildren(x, y, w, h);
        double togglesHeight;
        double togglesWidth;
        if (!this.getSkinnable().getStyleClass().contains("bullet-button")) {
            togglesHeight = this.toggleStatus.prefHeight(-1.0D);
            togglesWidth = this.toggleLight.snappedLeftInset() + this.snapSize(this.toggleLight.prefWidth(-1.0D)) + this.toggleLight.snappedRightInset();
            this.toggleStatus.resize(togglesWidth, togglesHeight);
            double width = this.toggleStatus.getLayoutBounds().getWidth();
            double centerX = x + (w - width) / 2.0D;
            this.positionInArea(this.toggleStatus, centerX, y + this.snappedBottomInset(), togglesWidth, h, 0.0D, HPos.CENTER, VPos.BOTTOM);
        } else {
            togglesHeight = this.toggleStatus.prefWidth(-1.0D);
            togglesWidth = this.toggleStatus.prefHeight(-1.0D);
            this.toggleStatus.resize(togglesHeight, togglesWidth);
            double yOffset = (h - togglesWidth) / 2.0D + y;
            this.positionInArea(this.toggleStatus, x, yOffset, togglesHeight, togglesWidth, 0.0D, this.toggleStatus.getAlignment().getHpos(), this.toggleStatus.getAlignment().getVpos());
        }

    }
}
