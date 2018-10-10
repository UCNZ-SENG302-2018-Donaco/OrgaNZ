package com.humanharvest.organz.skin;

import static com.humanharvest.organz.touch.TouchUtils.convertTouchEvent;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import org.tuiofx.widgets.skin.TextAreaSkinAndroid;

public class MTTextAreaSkin extends TextAreaSkinAndroid {

    private final ClickHelper clickHelper = new ClickHelper();

    public MTTextAreaSkin(TextArea textInput) {
        super(textInput);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            clickHelper.calculateClickCount(event);

            EventTarget eventTarget = findTextArea(event.getTarget());
            getBehavior().mousePressed(convertTouchEvent(event, eventTarget, clickHelper.getClickCount(),
                    MouseEvent.MOUSE_PRESSED));
            event.consume();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {

            EventTarget eventTarget = findTextArea(event.getTarget());
            getBehavior().mouseReleased(convertTouchEvent(event, eventTarget, clickHelper.getClickCount(),
                    MouseEvent.MOUSE_RELEASED));
            event.consume();
        });
    }

    private static EventTarget findTextArea(EventTarget target) {

        EventTarget node = target;

        while (node instanceof Node) {
            if (node instanceof TextArea) {
                return node;
            }

            node = ((Node)node).getParent();
        }

        return target;
    }
}
