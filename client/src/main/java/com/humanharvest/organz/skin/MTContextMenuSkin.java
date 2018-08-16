package com.humanharvest.organz.skin;

import com.humanharvest.organz.MultitouchHandler;
import com.sun.javafx.scene.control.skin.ContextMenuContent;
import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.transform.Rotate;
import org.tuiofx.widgets.utils.Util;

import java.util.Objects;
import java.util.Optional;

public class MTContextMenuSkin extends ContextMenuSkin {
    private final ContextMenu popupMenu;

    public MTContextMenuSkin(ContextMenu popupMenu) {
        super(popupMenu);
        this.popupMenu = popupMenu;

        popupMenu.setAutoHide(false);

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

                Node owner = getSkinnable().getOwnerNode();
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
}
