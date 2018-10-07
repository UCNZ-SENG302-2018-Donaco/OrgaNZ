package com.humanharvest.organz.touch;

import javafx.geometry.Point2D;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.TouchPoint;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import com.humanharvest.organz.controller.spiderweb.OrganWithRecipients;

public class MatchesFocusArea extends FocusArea {

    private final OrganWithRecipients organWithRecipients;
    private final OrganFocusArea organFocusArea;

    public MatchesFocusArea(Pane matchesPane, OrganWithRecipients organWithRecipients, OrganFocusArea organFocusArea) {
        super(matchesPane);
        this.organWithRecipients = organWithRecipients;
        this.organFocusArea = organFocusArea;

        organFocusArea.setMatchesFocusArea(this);
    }

    @Override
    protected void handleDoubleTouch(
            Point2D touchPointPosition,
            TouchPoint touchPoint,
            CurrentTouch currentTouch,
            CurrentTouch otherTouch) {
        // Only allow the matches area to be translate/rotated if it's organ image isn't currently touched
        if (!organFocusArea.isSpecificallyTouched()) {
            super.handleDoubleTouch(touchPointPosition, touchPoint, currentTouch, otherTouch);
        }
    }

    @Override
    protected void onTouchReleased(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchReleased(event, currentTouch);
        organWithRecipients.handleTouchReleased();
    }

    @Override
    protected void handleScale(double scaleDelta, Point2D centre) {
        if (isScalable() && organFocusArea.isScalable()) {

            Transform transform = getTransform();

            Affine oldTransform = new Affine(transform);

            Scale scaleTransform = new Scale(scaleDelta, scaleDelta, centre.getX(), centre.getY());
            organFocusArea.prependTransform(scaleTransform);
            prependTransform(scaleTransform);

            double currentMxx = transform.getMxx();
            double currentMxy = transform.getMxy();
            double scaleX = Math.sqrt(currentMxx * currentMxx + currentMxy * currentMxy);

            if (scaleX < 0.25 || scaleX > 2) {
                organFocusArea.setTransform(oldTransform);
                setTransform(oldTransform);
            }
        }
    }

    @Override
    protected void handleTranslate(Point2D currentCentre, Point2D lastCentre) {
        if (isTranslatable() && organFocusArea.isTranslatable()) {
            Point2D delta = currentCentre.subtract(lastCentre);
            delta = handleBoundsCheck(delta);
            Translate translation = new Translate(delta.getX(), delta.getY());

            organFocusArea.prependTransform(translation);
            prependTransform(translation);
        }
    }

    @Override
    protected void handleRotate(double angleDelta, Point2D centre) {
        if (isRotatable() && organFocusArea.isRotatable()) {
            Rotate rotation = new Rotate(Math.toDegrees(angleDelta), centre.getX(), centre.getY());
            organFocusArea.prependTransform(rotation);
            prependTransform(rotation);
        }
    }
}
