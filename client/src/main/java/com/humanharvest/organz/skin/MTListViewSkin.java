package com.humanharvest.organz.skin;

import static com.humanharvest.organz.touch.TouchUtils.convertTouchEvent;

import java.util.Objects;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;

import com.sun.javafx.scene.control.skin.ListCellSkin;
import com.sun.javafx.scene.control.skin.ListViewSkin;

public class MTListViewSkin<T> extends ListViewSkin<T> {

    private static final PseudoClass PRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("pressed");

    private final ClickHelper clickHelper = new ClickHelper();

    public MTListViewSkin(ListView<T> listView) {
        super(listView);

        getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, event -> {
            MouseEvent mouseEvent = convertTouchEvent(event, event.getTarget(), 1, MouseEvent.MOUSE_PRESSED);
            getBehavior().mousePressed(mouseEvent);
            event.consume();

            Cell<T> cell = findCell(event, listView);
            if (cell != null) {
                ListCellSkin<?> skin = (ListCellSkin<?>) cell.getSkin();
                skin.pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, true);
                mouseEvent = mouseEvent.copyFor(cell, mouseEvent.getTarget());
                skin.getBehavior().mousePressed(mouseEvent);
            }
        });
        getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, event -> {
            clickHelper.calculateClickCount(event);

            MouseEvent mouseEvent = convertTouchEvent(event, event.getTarget(),
                    clickHelper.getClickCount(),
                    MouseEvent.MOUSE_RELEASED);

            getBehavior().mouseReleased(mouseEvent);
            if (listView.getOnMouseClicked() != null) {
                listView.getOnMouseClicked().handle(mouseEvent);
            }
            event.consume();

            Cell<T> cell = findCell(event, listView);
            if (cell != null) {
                ListCellSkin<?> skin = (ListCellSkin<?>) cell.getSkin();
                skin.pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, false);
                mouseEvent = mouseEvent.copyFor(cell, mouseEvent.getTarget());
                skin.getBehavior().mouseReleased(mouseEvent);
            }
        });
    }

    private Cell<T> findCell(TouchEvent event, ListView<T> listView) {
        Node node = (Node) event.getTarget();
        while (!Objects.equals(node, listView) && node != null) {
            if (node instanceof Cell) {
                return (Cell<T>) node;
            }

            node = node.getParent();
        }

        return null;
    }
}
