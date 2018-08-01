package com.humanharvest.organz.utilities.view.tuiofx.skin;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextInputControl;

public class OnScreenKeyboard<T extends TextInputControl> extends Control {
    public static final String KB_GROUP_NAME_PROP = "kbGroup";
    public static final String KB_USE_FOCUS_AREA_PROP = "useFocusArea";
    private ObjectProperty<Node> attachedNode = null;

    public OnScreenKeyboard() {
        this.init();
    }

    private void init() {
        this.setSkin(new OnScreenKeyBoardSkin(this));
    }

    final ObjectProperty<Node> attachedNodeProperty() {
        if (this.attachedNode == null) {
            this.attachedNode = new ObjectPropertyBase<Node>() {
                public Object getBean() {
                    return this;
                }

                public String getName() {
                    return "attachedNode";
                }
            };
        }

        return this.attachedNode;
    }

    final Node getAttachedNode() {
        return this.attachedNode == null ? null : this.attachedNode.getValue();
    }

    final void setAttachedNode(Node value) {
        this.attachedNodeProperty().setValue(value);
    }

    public void attach(T textInput) {
        this.setAttachedNode(textInput);
    }

    public void detach() {
        this.setAttachedNode(null);
    }

    protected Skin<?> createDefaultSkin() {
        return new OnScreenKeyBoardSkin(this);
    }

    public boolean isShowing() {
        return ((OnScreenKeyBoardSkin) this.getSkin()).isVisible();
    }
}