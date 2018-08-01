package com.humanharvest.organz.utilities.view.tuiofx.skin;


import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.TouchEvent;

public class MTButtonSkin extends ButtonSkin {
    public MTButtonSkin(Button button) {
        super(button);
        EventHandler<TouchEvent> handler = event -> {
            if (MTButtonSkin.this.getSkinnable().isArmed()) {
                MTButtonSkin.this.getSkinnable().disarm();
            } else {
                MTButtonSkin.this.getSkinnable().arm();
            }

        };
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, handler);
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, handler);
    }
}
