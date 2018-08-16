package com.humanharvest.organz.skin;

import com.humanharvest.organz.MultitouchHandler;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.transform.Rotate;
import org.tuiofx.widgets.utils.Util;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class MTDatePickerSkin extends DatePickerSkin {

    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");

    private DatePicker datePicker;

    private final PopupControl popupMenu;


    private boolean isShowing;
    private BooleanProperty touchPressed = new BooleanPropertyBase(false) {
        protected void invalidated() {
            getPopupContent().pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, this.get());
        }

        public Object getBean() {
            return this;
        }

        public String getName() {
            return "pressed";
        }
    };

    public MTDatePickerSkin(DatePicker datePicker) {
        super(datePicker);
        this.datePicker = datePicker;

        this.popupMenu = getPopup();

        if (popupMenu.getOwnerNode() != null) {
            addOwnerNodeHandlers(popupMenu.getOwnerNode());
        } else {
            popupMenu.ownerNodeProperty().addListener((observable, oldValue, newValue) -> {
                addOwnerNodeHandlers(popupMenu.getOwnerNode());
            });
        }


        arrowButton.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            System.out.println("Arrow event");
            if (event.isSynthesized()) {
                getBehavior().mouseEntered(event);
            }
        });

        if (popupMenu.getOwnerNode() != null) {
            addOwnerNodeHandlers(popupMenu.getOwnerNode());
        } else {
            popupMenu.ownerNodeProperty().addListener((observable, oldValue, newValue) -> {
                addOwnerNodeHandlers(popupMenu.getOwnerNode());
            });
        }

        popupMenu.addEventHandler(Menu.ON_SHOWN, event -> {
            Node cmContent = popupMenu.getSkin().getNode();
            if (cmContent != null || !popupMenu.getStyleClass().contains("text-input-context-menu")) {

                Node owner = popupMenu.getOwnerNode();
                double angle = Util.getRotationDegreesLocalToScene(owner);
                double offsetY = 0.0D;
                if (cmContent instanceof ContextMenuContent) {
                    offsetY = getMenuYOffset(cmContent) - 5.0D;
                }

                if ((int) Math.abs(angle) == 0 || cmContent == null) {
                    return;
                }

                cmContent.getTransforms().setAll(new Rotate(angle));
                Rotate rotate = new Rotate(angle);
                Point2D transformedPoint = rotate.transform(0.0D, offsetY);
                double popupTopLeftX = owner.getLocalToSceneTransform().getTx();
                double popupTopLeftY = owner.getLocalToSceneTransform().getTy();
                double anchorX = popupTopLeftX + transformedPoint.getX() + Util.getOffsetX(owner);
                double anchorY = popupTopLeftY + transformedPoint.getY() + Util.getOffsetY(owner);
                popupMenu.setAnchorX(anchorX);
                popupMenu.setAnchorY(anchorY);
            }
        });


//        getPopupContent().focusedProperty().addListener((observable, oldValue, newValue) -> {
//            System.out.println("Focused");
//            Node owner = getSkinnable();
//            double offsetY = getSkinnable().prefHeight(-1.0D);
//            double angle = Util.getRotationDegreesLocalToScene(owner);
//            getPopupContent().getTransforms().setAll(new Rotate(angle));
//            Rotate rotate = new Rotate(angle);
//            Point2D transformedPoint = rotate.transform(0.0D, offsetY);
//            double popupTopLeftX = owner.getLocalToSceneTransform().getTx();
//            double popupTopLeftY = owner.getLocalToSceneTransform().getTy();
//            double anchorX = popupTopLeftX + transformedPoint.getX() + Util.getOffsetX(owner);
//            double anchorY = popupTopLeftY + transformedPoint.getY() + Util.getOffsetY(owner);
//            getPopup().setAnchorX(anchorX);
//            getPopup().setAnchorY(anchorY);
//        });
//
//        final ComboBoxBase<LocalDate> comboBoxBase = getSkinnable();
//        Node focusAreaNode = Util.getFocusAreaStartingNode(comboBoxBase);
//        if (focusAreaNode != null) {
//            focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
//                System.out.println("Mouse pressed");
//                if (!event.isSynthesized() && !isComboBoxOrButton(event.getTarget(), comboBoxBase)) {
//                    handleAutoHidingEvents();
//                }
//
//            });
//            focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
//                if (!isComboBoxOrButton(event.getTarget(), comboBoxBase)) {
//                    handleAutoHidingEvents();
//                }
//
//            });
//            comboBoxBase.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
//                System.out.println("Mouse released");
//                if (!event.isSynthesized() && isComboBoxOrButton(event.getTarget(), comboBoxBase) && isShowing) {
//                    handleAutoHidingEvents();
//                    isShowing = false;
//                } else {
//                    isShowing = true;
//                }
//
//            });
//            getBehavior().getControl().showingProperty().addListener((observable, oldValue, newValue) -> {
//                System.out.println("Showing" + newValue);
//                if (!newValue) {
//                    getPopup().setAutoHide(false);
//                }
//
//            });
//            datePicker.getScene().getWindow().focusedProperty().addListener((observable, oldValue, newValue) -> {
//                System.out.println("Window focused" + newValue);
//                if (!newValue) {
//                    handleAutoHidingEvents();
//                }
//
//            });
//            this.getPopupContent().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> touchPressed.setValue(true));
//        }

    }



    private double getMenuYOffset(Node cmContent) {
        ContextMenuContent contextContent = (ContextMenuContent) cmContent;
        double offset = 0.0D;
        if (contextContent.getItemsContainer().getChildren().size() > 0) {
            Node menuitem = contextContent.getItemsContainer().getChildren().get(0);
            offset = contextContent.snappedTopInset() + menuitem.getLayoutY() + menuitem.prefHeight(-1.0D);
        }

        return offset;
    }

    private void addOwnerNodeHandlers(Node owner) {
        popupMenu.setAutoHide(false);
        Node focusAreaNode = Util.getFocusAreaStartingNode(owner);
        if (focusAreaNode != null) {
            MultitouchHandler
                    .getFocusAreaHandler(owner)
                    .ifPresent(focusAreaHandler -> {
                        focusAreaHandler.addPopup(popupMenu, this::handleAutoHidingEvents);
                    });
            focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, event -> {
                handleAutoHidingEvents(Optional.ofNullable(event).map(Event::getTarget).orElse(null));
            });
            focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!event.isSynthesized()) {
                    handleAutoHidingEvents(Optional.of(event).map(Event::getTarget).orElse(null));
                }
            });
        }

        owner.getScene().getWindow().focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (!newValue1) {
                handleAutoHidingEvents(null);
            }
        });
    }

    private void handleAutoHidingEvents(EventTarget eventTarget) {
        Node owner = popupMenu.getOwnerNode();
        if (eventTarget != null && owner instanceof ChoiceBox) {
            Node targetNode = (Node) eventTarget;
            if (isPressingChoiceBoxButton(targetNode)) {
                return;
            }
        }

        popupMenu.hide();
        if (owner instanceof ChoiceBox) {
            ((ChoiceBox<?>) owner).hide();
        }
    }

    private boolean isPressingChoiceBoxButton(Node targetNode) {
        Node owner = popupMenu.getOwnerNode();
        if (Objects.equals(targetNode, owner)) {
            return true;
        } else {
            for (Parent parent = targetNode.getParent(); parent != null; parent = parent.getParent()) {
                if (Objects.equals(owner, parent)) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isComboBoxOrButton(EventTarget target, ComboBoxBase<LocalDate> comboBoxBase) {
        System.out.println("Checked is cmbo or btn");
        System.out.println(target instanceof Node && "arrow-button".equals(((Node) target).getId()) || comboBoxBase.equals(target));
        return target instanceof Node && "arrow-button".equals(((Node) target).getId()) || comboBoxBase.equals(target);
    }

    private void handleAutoHidingEvents() {
        System.out.println("handling auto hide");
        if (this.getSkinnable().isShowing()) {
            this.getPopup().hide();
            this.getSkinnable().hide();
            this.isShowing = false;
        }
    }
}
