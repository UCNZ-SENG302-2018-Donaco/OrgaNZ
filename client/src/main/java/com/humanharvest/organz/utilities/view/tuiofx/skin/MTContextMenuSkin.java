package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.humanharvest.organz.utilities.view.tuiofx.skin.tuiofx.Util;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;

public class MTContextMenuSkin extends ContextMenuSkin {
    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");
    private final ContextMenu popupMenu;

    public MTContextMenuSkin(final ContextMenu popupMenu) {
        super(popupMenu);
        this.popupMenu = popupMenu;
        popupMenu.addEventHandler(Menu.ON_SHOWN, event -> {
            Node cmContent = popupMenu.getSkin().getNode();
            if (cmContent != null || !popupMenu.getStyleClass().contains("text-input-context-menu")) {
                if (cmContent instanceof ContextMenuContent) {
                    VBox accMenu = ((ContextMenuContent) cmContent).getItemsContainer();

                    for (Node child : accMenu.getChildren()) {
                        child.addEventHandler(TouchEvent.TOUCH_PRESSED, event12 -> child.pseudoClassStateChanged(MTContextMenuSkin.PRESSED_PSEUDO_CLASS, true));
                        child.addEventHandler(TouchEvent.TOUCH_RELEASED, event1 -> child.pseudoClassStateChanged(MTContextMenuSkin.PRESSED_PSEUDO_CLASS, false));
                    }
                }

                Node owner = MTContextMenuSkin.this.getSkinnable().getOwnerNode();
                double angle = Util.getRotationDegreesLocalToScene(owner);
                double offsetY = 0.0D;
                if (cmContent instanceof ContextMenuContent) {
                    offsetY = MTContextMenuSkin.this.getMenuYOffset(cmContent) - 5.0D;
                }

                if ((int) Math.abs(angle) == 0) {
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
        popupMenu.setAutoHide(false);
        popupMenu.ownerNodeProperty().addListener((observable, oldValue, newValue) -> {
            popupMenu.setAutoHide(false);
            Node owner = popupMenu.getOwnerNode();
            Node focusAreaNode = Util.getFocusAreaStartingNode(owner);
            if (focusAreaNode != null) {
                focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, MTContextMenuSkin.this::handleAutoHidingEvents);
                focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!event.isSynthesized()) {
                        MTContextMenuSkin.this.handleAutoHidingEvents(event);
                    }

                });
            }

            owner.getScene().getWindow().focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
                if (!newValue1) {
                    MTContextMenuSkin.this.handleAutoHidingEvents(null);
                }

            });
        });
    }

    private void handleAutoHidingEvents(Event event) {
        Node owner = this.popupMenu.getOwnerNode();
        if (event != null && owner instanceof ChoiceBox) {
            Node targetNode = (Node) event.getTarget();
            if (this.isPressingChoiceBoxButton(targetNode)) {
                return;
            }
        }

        this.popupMenu.hide();
        if (owner instanceof ChoiceBox) {
            ((ChoiceBox) owner).hide();
        }

    }

    private boolean isPressingChoiceBoxButton(Node targetNode) {
        Node owner = this.popupMenu.getOwnerNode();
        if (targetNode.equals(owner)) {
            return true;
        } else {
            for (Parent parent = targetNode.getParent(); parent != null; parent = parent.getParent()) {
                if (owner.equals(parent)) {
                    return true;
                }
            }

            return false;
        }
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
}
