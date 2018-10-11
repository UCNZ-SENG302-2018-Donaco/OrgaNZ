package com.humanharvest.organz.skin;

import javafx.scene.control.Button;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ButtonSkin;

public class MTButtonSkin extends ButtonSkin implements IgnoreSynthesized {
    public MTButtonSkin(Button button) {
        super(button);

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
