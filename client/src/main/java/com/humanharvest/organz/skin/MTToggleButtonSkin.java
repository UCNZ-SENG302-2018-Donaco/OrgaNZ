package com.humanharvest.organz.skin;

import javafx.scene.control.ToggleButton;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ToggleButtonSkin;

public class MTToggleButtonSkin extends ToggleButtonSkin implements IgnoreSynthesized {

    public MTToggleButtonSkin(ToggleButton toggleButton) {
        super(toggleButton);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            getSkinnable().arm();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            if (getSkinnable().isArmed()) {
                getSkinnable().fire();
                getSkinnable().disarm();
            }
        });
    }
}
