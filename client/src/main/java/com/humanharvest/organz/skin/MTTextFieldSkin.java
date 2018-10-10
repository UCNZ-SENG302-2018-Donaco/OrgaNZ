package com.humanharvest.organz.skin;

import static com.humanharvest.organz.touch.TouchUtils.convertTouchEvent;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import org.tuiofx.widgets.skin.TextFieldSkinAndroid;

public class MTTextFieldSkin extends TextFieldSkinAndroid {

    private final ClickHelper clickHelper = new ClickHelper();

    public MTTextFieldSkin(TextField textInput) {
        super(textInput);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            clickHelper.calculateClickCount(event);

            EventTarget eventTarget = findTextField(event.getTarget());
            getBehavior().mousePressed(convertTouchEvent(event, eventTarget, clickHelper.getClickCount(),
                    MouseEvent.MOUSE_PRESSED));
            event.consume();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            EventTarget eventTarget = findTextField(event.getTarget());
            getBehavior().mouseReleased(convertTouchEvent(event, eventTarget, clickHelper.getClickCount(),
                    MouseEvent.MOUSE_RELEASED));
            event.consume();
        });
    }

    private EventTarget findTextField(EventTarget target) {

        EventTarget node = target;

        while (node instanceof Node) {
            if (node instanceof TextField) {
                return node;
            }

            node = ((Node)node).getParent();
        }

        return target;
    }
}


