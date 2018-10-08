package com.humanharvest.organz.skin;

import javafx.scene.control.Button;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import org.tuiofx.widgets.controls.MultiKeyButton;
import org.tuiofx.widgets.event.KeyButtonEvent;

public class MTKeyboardButtonSkin extends ButtonSkin {
    public MTKeyboardButtonSkin(Button button) {
        super(button);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            System.out.println("KEY PRESSED");
            getSkinnable().arm();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            System.out.println("KEY RELEASED");
            MultiKeyButton keyboardButton = (MultiKeyButton) getSkinnable();
            keyboardButton.fireEvent(new KeyButtonEvent(keyboardButton, KeyButtonEvent.SHORT_PRESSED));
            getSkinnable().disarm();
        });
    }
}
