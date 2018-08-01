package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.humanharvest.organz.utilities.view.tuiofx.skin.tuiofx.Util;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.transform.Rotate;

public class MTComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {
    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");
    private final ComboBox comboBox;
    private boolean isShowing = false;
    private BooleanProperty touchPressed = new BooleanPropertyBase(false) {
        protected void invalidated() {
            MTComboBoxListViewSkin.this.getPopupContent().pseudoClassStateChanged(MTComboBoxListViewSkin.PRESSED_PSEUDO_CLASS, this.get());
        }

        public Object getBean() {
            return MTComboBoxListViewSkin.this;
        }

        public String getName() {
            return "pressed";
        }
    };

    public MTComboBoxListViewSkin(ComboBox comboBox) {
        super(comboBox);
        this.comboBox = comboBox;
        this.getPopup().setAutoHide(false);
        this.arrowButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (event.isSynthesized()) {
                MTComboBoxListViewSkin.this.getBehavior().mouseEntered(event);
            }

        });
        this.getPopupContent().focusedProperty().addListener((observable, oldValue, newValue) -> {
            Node owner = MTComboBoxListViewSkin.this.getSkinnable();
            double offsetY = MTComboBoxListViewSkin.this.getSkinnable().prefHeight(-1.0D);
            double angle = Util.getRotationDegreesLocalToScene(owner);
            MTComboBoxListViewSkin.this.getPopupContent().getTransforms().setAll(new Rotate(angle));
            Rotate rotate = new Rotate(angle);
            Point2D transformedPoint = rotate.transform(0.0D, offsetY);
            double popupTopLeftX = owner.getLocalToSceneTransform().getTx();
            double popupTopLeftY = owner.getLocalToSceneTransform().getTy();
            double anchorX = popupTopLeftX + transformedPoint.getX() + Util.getOffsetX(owner);
            double anchorY = popupTopLeftY + transformedPoint.getY() + Util.getOffsetY(owner);
            MTComboBoxListViewSkin.this.getPopup().setAnchorX(anchorX);
            MTComboBoxListViewSkin.this.getPopup().setAnchorY(anchorY);
        });
        final ComboBoxBase<T> comboBoxBase = this.getSkinnable();
        Node focusAreaNode = Util.getFocusAreaStartingNode(comboBoxBase);
        if (focusAreaNode != null) {
            focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!event.isSynthesized() && !MTComboBoxListViewSkin.this.isComboBoxOrButton(event.getTarget(), comboBoxBase)) {
                    MTComboBoxListViewSkin.this.handleAutoHidingEvents();
                }

            });
            focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
                if (!MTComboBoxListViewSkin.this.isComboBoxOrButton(event.getTarget(), comboBoxBase)) {
                    MTComboBoxListViewSkin.this.handleAutoHidingEvents();
                }

            });
            comboBoxBase.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
                if (!event.isSynthesized() && MTComboBoxListViewSkin.this.isComboBoxOrButton(event.getTarget(), comboBoxBase) && MTComboBoxListViewSkin.this.isShowing) {
                    MTComboBoxListViewSkin.this.handleAutoHidingEvents();
                    MTComboBoxListViewSkin.this.isShowing = false;
                } else {
                    MTComboBoxListViewSkin.this.isShowing = true;
                }

            });
            ((ComboBoxBase) ((ComboBoxBaseBehavior) this.getBehavior()).getControl()).showingProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    MTComboBoxListViewSkin.this.getPopup().setAutoHide(false);
                }

            });
            comboBox.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    MTComboBoxListViewSkin.this.handleAutoHidingEvents();
                }

            });
            this.getPopupContent().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> MTComboBoxListViewSkin.this.touchPressed.setValue(true));
        }
    }

    private boolean isComboBoxOrButton(EventTarget target, ComboBoxBase<T> comboBoxBase) {
        return target instanceof Node && "arrow-button".equals(((Node) target).getId()) || comboBoxBase.equals(target);
    }

    private void handleAutoHidingEvents() {
        if (this.getSkinnable().isShowing()) {
            this.getPopup().hide();
            this.getSkinnable().hide();
            this.isShowing = false;
        }

    }
}
