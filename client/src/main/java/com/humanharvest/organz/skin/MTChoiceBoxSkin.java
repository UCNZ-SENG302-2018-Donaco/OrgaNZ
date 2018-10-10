package com.humanharvest.organz.skin;

import javafx.css.PseudoClass;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ChoiceBoxSkin;

public class MTChoiceBoxSkin<T> extends ChoiceBoxSkin<T> {

    public MTChoiceBoxSkin(ChoiceBox<T> checkbox) {
        super(checkbox);

        getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
            System.out.println("touch pressed on choice box");
            getSkinnable().pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), true);
            event.consume();
        });

        getSkinnable().addEventFilter(TouchEvent.TOUCH_RELEASED, event -> {
            System.out.println("touch released on choice box");
            getSkinnable().pseudoClassStateChanged(PseudoClass.getPseudoClass("pressed"), false);
            if (getSkinnable().isShowing()) {
                getSkinnable().hide();
            } else {
                getSkinnable().show();
            }
            event.consume();
        });
    }

}
