package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.skin.CheckBoxSkin;
import javafx.scene.control.CheckBox;
import javafx.scene.input.TouchEvent;

public class MTCheckBoxSkin extends CheckBoxSkin {
    public MTCheckBoxSkin(CheckBox checkbox) {
        super(checkbox);
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> MTCheckBoxSkin.this.getSkinnable().arm());
    }
}
