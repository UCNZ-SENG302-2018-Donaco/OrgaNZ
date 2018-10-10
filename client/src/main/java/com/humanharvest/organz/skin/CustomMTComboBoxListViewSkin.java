package com.humanharvest.organz.skin;

import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.input.TouchEvent;

import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;

public class CustomMTComboBoxListViewSkin<T> extends MTComboBoxListViewSkin<T> {

    public CustomMTComboBoxListViewSkin(ComboBox<T> comboBox) {
        super(comboBox);

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

    /**
     * This is the main check, we need to see if this is a CheckComboBox and if so do not minimize.
     *
     * @return A boolean indicating if clicking an element should close the popup
     */
    @Override
    protected boolean isHideOnClickEnabled() {
        return !getSkinnable().toString().contains("CheckComboBox");
    }
}
