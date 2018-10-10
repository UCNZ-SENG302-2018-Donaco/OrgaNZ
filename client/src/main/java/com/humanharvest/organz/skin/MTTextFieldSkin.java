package com.humanharvest.organz.skin;

import javafx.event.EventType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import org.tuiofx.widgets.skin.TextFieldSkinAndroid;

public class MTTextFieldSkin extends TextFieldSkinAndroid {

    public MTTextFieldSkin(TextField textInput) {
        super(textInput);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            getBehavior().mousePressed(convertTouchEvent(event, MouseEvent.MOUSE_PRESSED));
            event.consume();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            getBehavior().mouseReleased(convertTouchEvent(event, MouseEvent.MOUSE_RELEASED));
            event.consume();
        });
    }

    private static MouseEvent convertTouchEvent(TouchEvent event, EventType<? extends MouseEvent> eventType) {

        // TODO: ClickCount

        MouseEvent newEvent = new MouseEvent(eventType, event.getTouchPoint().getSceneX(),
                event.getTouchPoint().getSceneY(),
                event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY(), MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false,
                true, false, true, event.getTouchPoint().getPickResult());
        return newEvent.copyFor(event.getSource(), event.getTarget());
    }
}
