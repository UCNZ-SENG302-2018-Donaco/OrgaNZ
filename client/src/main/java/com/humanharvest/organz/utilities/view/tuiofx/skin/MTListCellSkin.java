package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.skin.ListCellSkin;
import javafx.css.PseudoClass;
import javafx.scene.control.ListCell;
import javafx.scene.input.TouchEvent;

public class MTListCellSkin extends ListCellSkin {
    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");

    public MTListCellSkin(ListCell control) {
        super(control);
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> MTListCellSkin.this.pseudoClassStateChanged(MTListCellSkin.PRESSED_PSEUDO_CLASS, true));
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, event -> MTListCellSkin.this.pseudoClassStateChanged(MTListCellSkin.PRESSED_PSEUDO_CLASS, false));
    }
}
