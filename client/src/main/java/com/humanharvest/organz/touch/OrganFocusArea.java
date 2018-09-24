package com.humanharvest.organz.touch;

import javafx.geometry.Point2D;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;

public class OrganFocusArea extends FocusArea {

    private static final double MAX_CLICK_DISTANCE = 25;
    private Point2D originalTouchPoint;
    private boolean countsAsClick;

    public OrganFocusArea(Pane pane) {
        super(pane);
    }

    @Override
    protected void onTouchPressed(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchPressed(event, currentTouch);

        if (getPaneTouches().size() == 1) {
            originalTouchPoint = new Point2D(event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY());
            countsAsClick = true;
        } else {
            countsAsClick = false;
        }
    }

    @Override
    protected void onTouchReleased(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchReleased(event, currentTouch);

        if (countsAsClick) {
            System.out.println("ASKDJHHASD");
        }
    }

    @Override
    protected void onTouchHeld(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchHeld(event, currentTouch);

        if (countsAsClick) {
            Point2D newTouchPoint = new Point2D(event.getTouchPoint().getScreenX(), event.getTouchPoint().getScreenY());
            if (PointUtils.distance(newTouchPoint, originalTouchPoint) > MAX_CLICK_DISTANCE) {
                countsAsClick = false;
            }
        }
    }
}
