package com.humanharvest.organz.utilities.view.tuiofx.skin;


import com.sun.javafx.scene.control.skin.RadioButtonSkin;
import javafx.scene.control.RadioButton;
import javafx.scene.input.TouchEvent;

public class MTRadioButtonSkin extends RadioButtonSkin {
    public MTRadioButtonSkin(RadioButton radioButton) {
        super(radioButton);
        this.getSkinnable().addEventFilter(TouchEvent.TOUCH_PRESSED, event -> MTRadioButtonSkin.this.getSkinnable().arm());
    }
}
