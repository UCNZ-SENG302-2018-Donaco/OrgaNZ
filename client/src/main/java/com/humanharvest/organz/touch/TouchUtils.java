package com.humanharvest.organz.touch;

import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

public class TouchUtils {
    public static MouseEvent convertTouchEvent(TouchEvent event, EventTarget target,
            EventType<? extends MouseEvent> eventType) {

        // TODO: ClickCount

        MouseEvent newEvent = new MouseEvent(eventType,
                event.getTouchPoint().getSceneX(), event.getTouchPoint().getSceneY(),
                event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY(),
                MouseButton.PRIMARY, 1,
                false, false, false, false,
                true, false, false,
                true, false, true,
                event.getTouchPoint().getPickResult());
        return newEvent.copyFor(event.getSource(), target);
    }
}
