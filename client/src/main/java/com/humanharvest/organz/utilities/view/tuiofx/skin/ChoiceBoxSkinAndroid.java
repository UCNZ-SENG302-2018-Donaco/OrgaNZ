package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.TouchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoiceBoxSkinAndroid<T> extends ChoiceBoxSkin<T> {
    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");
    private final ChoiceBox choiceBox;

    private static Logger logger = LoggerFactory.getLogger(ChoiceBoxSkinAndroid.class);


    private BooleanProperty touchPressed = new BooleanPropertyBase(false) {
        protected void invalidated() {
            ChoiceBoxSkinAndroid.this.pseudoClassStateChanged(ChoiceBoxSkinAndroid.PRESSED_PSEUDO_CLASS, this.get());
        }

        public Object getBean() {
            return ChoiceBoxSkinAndroid.this;
        }

        public String getName() {
            return "pressed";
        }
    };

    public ChoiceBoxSkinAndroid(ChoiceBox choiceBox) {
        super(choiceBox);
        this.choiceBox = choiceBox;
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> ChoiceBoxSkinAndroid.this.touchPressed.set(true));
        this.getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, event -> ChoiceBoxSkinAndroid.this.touchPressed.set(false));
        this.getSkinnable().setFocusTraversable(false);
    }

    protected void layoutChildren(double x, double y, double w, double h) {
        Node openButton = null;
        Node label = null;

        for (Node child : this.getChildren()) {
            if (child.getStyleClass().get(0).equals("open-button")) {
                openButton = child;
            }

            if (child instanceof Label) {
                label = child;
            }
        }

        if (openButton == null || label == null) {
            logger.error("Failed to layout open-button or label");
        }

        double obw = openButton.prefWidth(-1.0D);
        label.resizeRelocate(x, y, w, h);
        openButton.resize(obw, openButton.prefHeight(-1.0D));
        this.positionInArea(openButton, x + w - obw, y, obw, h, 0.0D, HPos.CENTER, VPos.BOTTOM);
    }
}
