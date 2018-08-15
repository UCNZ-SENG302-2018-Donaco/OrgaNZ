package com.humanharvest.organz.skin;

import java.util.Objects;
import java.util.Optional;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import com.humanharvest.organz.MultitouchHandler;
import com.sun.javafx.scene.control.skin.ContextMenuSkin;
import org.tuiofx.widgets.utils.Util;

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
            Node targetNode = (Node)eventTarget;
            if (isPressingChoiceBoxButton(targetNode)) {
                return;
            }
        }

        popupMenu.hide();
        if (owner instanceof ChoiceBox) {
            ((ChoiceBox<?>)owner).hide();
        }
    }

    private boolean isPressingChoiceBoxButton(Node targetNode) {
        Node owner = popupMenu.getOwnerNode();
        if (Objects.equals(targetNode, owner)) {
            return true;
        } else {
            for(Parent parent = targetNode.getParent(); parent != null; parent = parent.getParent()) {
                if (Objects.equals(owner, parent)) {
                    return true;
                }
            }

            return false;
        }
    }
}
