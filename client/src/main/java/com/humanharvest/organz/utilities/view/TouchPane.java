package com.humanharvest.organz.utilities.view;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.awt.event.FocusEvent;

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
            System.out.println(this + "touched");
            if (touchId == -1) {
                touchId = event.getTouchPoint().getId();
                touchx = event.getTouchPoint().getSceneX() - getTranslateX();
                touchy = event.getTouchPoint().getSceneY() - getTranslateY();
            }
            event.consume();
        });

        setOnTouchReleased(event -> {
            System.out.println(this + "released");
            if (event.getTouchPoint().getId() == touchId) {
                touchId = -1;
            }
            event.consume();
        });

        setOnTouchMoved(event -> {
            System.out.println(this + "moved");
            if (event.getTouchPoint().getId() == touchId) {
                setTranslateX(event.getTouchPoint().getSceneX() - touchx);
                setTranslateY(event.getTouchPoint().getSceneY() - touchy);
            }
            event.consume();
        });

        setOnRotate(event -> {
            System.out.println("Rotated: " + event.isDirect());
            setRotate(this.getRotate() + event.getAngle());
        });

        setOnZoom(event -> {
            System.out.println("Zoomed: " + event.isDirect());
            setScaleX(this.getScaleX() * event.getZoomFactor());
            setScaleY(this.getScaleY() * event.getZoomFactor());
        });
    }
}