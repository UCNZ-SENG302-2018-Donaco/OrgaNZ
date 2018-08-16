package com.humanharvest.organz.skin;

import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import org.tuiofx.widgets.skin.MTComboBoxListViewSkin;

public class CustomMTComboBoxListViewSkin<T> extends MTComboBoxListViewSkin<T> {

    public CustomMTComboBoxListViewSkin(ComboBox<T> comboBox) {
        super(comboBox);
    }

    private boolean isComboBoxOrButton(EventTarget target, ComboBoxBase<T> comboBoxBase) {
        return target instanceof Node && "arrow-button".equals(((Node) target).getId()) || comboBoxBase.equals(target);
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