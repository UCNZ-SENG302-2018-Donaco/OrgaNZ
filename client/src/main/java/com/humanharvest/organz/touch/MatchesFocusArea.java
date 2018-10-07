package com.humanharvest.organz.touch;

import javafx.geometry.Point2D;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import com.humanharvest.organz.controller.spiderweb.OrganWithRecipients;

public class MatchesFocusArea extends FocusArea {

    private final OrganWithRecipients organWithRecipients;
    private final OrganFocusArea organFocusArea;

    public MatchesFocusArea(Pane matchesPane, OrganWithRecipients organWithRecipients, OrganFocusArea organFocusArea) {
        super(matchesPane);
        this.organWithRecipients = organWithRecipients;
        this.organFocusArea = organFocusArea;
    }

    @Override
    protected void onTouchReleased(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchReleased(event, currentTouch);
        organWithRecipients.handleTouchReleased();
    }

//    @Override
//    protected void handleScale(double scaleDelta, Point2D centre) {
//        Transform transform = getTransform();
//
//        Affine oldTransform = new Affine(transform);
//
//        Scale scaleTransform = new Scale(scaleDelta, scaleDelta, centre.getX(), centre.getY());
//        organFocusArea.prependTransform(scaleTransform);
//
//        double currentMxx = transform.getMxx();
//        double currentMxy = transform.getMxy();
//        double scaleX = Math.sqrt(currentMxx * currentMxx + currentMxy * currentMxy);
//
//        if (scaleX < 0.25 || scaleX > 2) {
//            organFocusArea.setTransform(oldTransform);
//        }
//    }

    @Override
    protected void handleRotate(double angleDelta, Point2D centre) {
        // Rotate the pane
        if (isRotatable()) {
            organFocusArea.prependTransform(new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY()));
        }
    }
}
