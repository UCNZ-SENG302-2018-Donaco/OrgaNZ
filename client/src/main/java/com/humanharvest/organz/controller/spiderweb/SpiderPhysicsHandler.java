package com.humanharvest.organz.controller.spiderweb;

import java.util.Objects;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.PhysicsHandler;
import com.humanharvest.organz.touch.PointUtils;

public class SpiderPhysicsHandler extends PhysicsHandler {

    private static final double MOVE_ASIDE_VELOCITY_PER_TICK = 2;
    private static final double MAX_MOVE_ASIDE_VELOCITY = 200;
    private static final double MOVE_ASIDE_DAMPING = 0.95;

    public SpiderPhysicsHandler(Pane rootPane) {
        super(rootPane);
    }

    @Override
    public void processPhysics() {

        super.processPhysics();

        for (FocusArea focusArea : MultitouchHandler.getFocusAreas()) {
            boolean paneTouched = !MultitouchHandler.findPaneTouches(focusArea.getPane()).isEmpty();

            // Skip if touched or not moveable
            if (paneTouched || !focusArea.isTranslatable() || !focusArea.isCollidable()) {
                continue;
            }

            Bounds bounds = focusArea.getPane().getBoundsInParent();
            Point2D centre = PointUtils.getCentreOfBounds(bounds);

            for (FocusArea otherFocusArea : MultitouchHandler.getFocusAreas()) {

                // If the other pane isn't moveable due to being touched or not collidable
                boolean otherNotMoveable = !MultitouchHandler.findPaneTouches(otherFocusArea.getPane()).isEmpty()
                        || !otherFocusArea.isCollidable();

                // Skip if the same object or not moveable
                if (Objects.equals(focusArea, otherFocusArea) || otherNotMoveable) {
                    continue;
                }

                Bounds otherBounds = otherFocusArea.getPane().getBoundsInParent();

                if (bounds.intersects(otherBounds)) {
                    // Adds velocity to the focus area to move it away from the other focus area.
                    Point2D otherCentre = PointUtils.getCentreOfBounds(otherBounds);
                    Point2D velocityDelta = centre.subtract(otherCentre).normalize();
                    focusArea.addVelocity(velocityDelta.multiply(MOVE_ASIDE_VELOCITY_PER_TICK));

                    // If the velocity is greater then MAX_MOVE_ASIDE_VELOCITY, dampen the velocity
                    double length = Math.abs(focusArea.getVelocity().distance(Point2D.ZERO));
                    if (length > MAX_MOVE_ASIDE_VELOCITY) {
                        focusArea.setVelocity(focusArea
                                .getVelocity()
                                .normalize()
                                .multiply(length * MOVE_ASIDE_DAMPING));
                    }
                }
            }
        }
    }
}
