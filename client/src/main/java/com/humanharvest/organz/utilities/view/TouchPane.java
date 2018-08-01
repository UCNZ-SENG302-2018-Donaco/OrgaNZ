package com.humanharvest.organz.utilities.view;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TouchPane extends Pane {

    private long touchId = -1;
    private double touchx, touchy;

    /**
     * Creates a TouchPane layout.
     */
    public TouchPane() {
        super();
        setup();
    }

    /**
     * Creates a TouchPane layout.
     *
     * @param children The initial set of children for this pane.
     * @since JavaFX 8.0
     */
    public TouchPane(Node... children) {
        super();
        getChildren().addAll(children);
        setup();
    }


    private void setup() {
        setEffect(new DropShadow(8.0, 4.5, 6.5, Color.DARKSLATEGRAY));

        setOnTouchPressed(event -> {
            if (touchId == -1) {
                touchId = event.getTouchPoint().getId();
                touchx = event.getTouchPoint().getSceneX() - getTranslateX();
                touchy = event.getTouchPoint().getSceneY() - getTranslateY();
            }
        });

        setOnTouchReleased(event -> {
            if (event.getTouchPoint().getId() == touchId) {
                touchId = -1;
            }
        });

        setOnTouchMoved(event -> {
            if (event.getTouchPoint().getId() == touchId) {
                setTranslateX(event.getTouchPoint().getSceneX() - touchx);
                setTranslateY(event.getTouchPoint().getSceneY() - touchy);
            }
        });
    }
}