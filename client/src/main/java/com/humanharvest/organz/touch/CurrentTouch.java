package com.humanharvest.organz.touch;

import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.tuiofx.widgets.controls.KeyboardPane;

/**
 * The state of a finger touching the screen.
 */
class CurrentTouch {

    private final Optional<Pane> pane;
    private final Optional<KeyboardPane> keyboardPane;
    private final Optional<Node> importantElement;

    private Point2D currentScreenPoint;
    private Point2D lastCentre;

    public CurrentTouch(Pane pane, KeyboardPane keyboardPane, Node importantElement) {
        this.pane = Optional.ofNullable(pane);
        this.keyboardPane = Optional.ofNullable(keyboardPane);
        this.importantElement = Optional.ofNullable(importantElement);
    }

    /**
     * Returns the pane this finger controls.
     */
    public Optional<Pane> getPane() {
        return pane;
    }

    /**
     * Returns the keyboard pane this finger controls.
     */
    public Optional<KeyboardPane> getKeyboardPane() {
        return keyboardPane;
    }

    /**
     * Returns the current point of the finger in screen coordinates.
     */
    public Point2D getCurrentScreenPoint() {
        return currentScreenPoint;
    }

    /**
     * Updates the current point of the finger.
     */
    public void setCurrentScreenPoint(Point2D currentScreenPoint) {
        this.currentScreenPoint = currentScreenPoint;
    }

    /**
     * Returns the last centre point between this finger and another one.
     */
    public Point2D getLastCentre() {
        return lastCentre;
    }

    public void setLastCentre(Point2D lastCentre) {
        this.lastCentre = lastCentre;
    }

    public Optional<Node> getImportantElement() {
        return importantElement;
    }
}
