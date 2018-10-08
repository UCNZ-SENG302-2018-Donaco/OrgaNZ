package com.humanharvest.organz.skin;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.TouchEvent;

import org.tuiofx.widgets.skin.TextFieldSkinAndroid;

public class MTTextFieldSkin extends TextFieldSkinAndroid {

    public MTTextFieldSkin(TextField textInput) {
        super(textInput);

        EventHandler<TouchEvent> handlePressHandler = e -> {
            double pressX = e.getTouchPoint().getX();
            double pressY = e.getTouchPoint().getY();
            e.consume();
        };

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            System.out.println("KEY PRESSED");
            getSkinnable().requestFocus();
            double pressX = event.getTouchPoint().getX();
            double pressY = event.getTouchPoint().getY();

//            getSkinnable().positionCaret();

            event.consume();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            System.out.println("KEY RELEASED");

        });
    }
}
