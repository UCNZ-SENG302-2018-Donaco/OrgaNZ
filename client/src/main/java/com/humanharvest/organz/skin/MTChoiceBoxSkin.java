package com.humanharvest.organz.skin;

import javafx.event.Event;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ChoiceBoxSkin;

public class MTChoiceBoxSkin<T> extends ChoiceBoxSkin<T> {

    public MTChoiceBoxSkin(ChoiceBox<T> checkbox) {
        super(checkbox);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, Event::consume);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            if (getSkinnable().isShowing()) {
                getSkinnable().hide();
            } else {
                getSkinnable().show();
            }
            event.consume();
        });
    }

}
