package com.humanharvest.organz.controller.components;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class MaskedView extends Control {

    private final SimpleObjectProperty<Node> content = new SimpleObjectProperty<>(this, "content");
    private final DoubleProperty fadingSize = new SimpleDoubleProperty(this, "fadingSize", 120);

    public MaskedView(Node content) {
        setContent(content);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new MaskedViewSkin(this);
    }

    public final Node getContent() {
        return content.get();
    }

    public final void setContent(Node content) {
        this.content.set(content);
    }

    public final SimpleObjectProperty<Node> contentProperty() {
        return content;
    }

    public final double getFadingSize() {
        return fadingSize.get();
    }

    public final void setFadingSize(double fadingSize) {
        this.fadingSize.set(fadingSize);
    }

    public final DoubleProperty fadingSizeProperty() {
        return fadingSize;
    }
}