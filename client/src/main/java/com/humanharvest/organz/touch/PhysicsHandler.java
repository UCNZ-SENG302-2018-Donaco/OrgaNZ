package com.humanharvest.organz.touch;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;

public class PhysicsHandler {

    public static final double MIN_VELOCITY_THRESHOLD = 10;
    public static final double COLLISION_VELOCITY_LOSS = 0.5;
    public static final double SURFACE_TENSION = 0.2;
    public static final long PHYSICS_MILLISECOND_PERIOD = 16;

    private final Pane rootPane;

    public PhysicsHandler(Pane rootPane) {

        this.rootPane = rootPane;
    }

    /**
     * Processes physics for the focus areas.
     */
    public void processPhysics() {

        for (FocusArea focusArea : MultitouchHandler.getFocusAreas()) {
            boolean paneNotTouched = MultitouchHandler.findPaneTouches(focusArea.getPane()).isEmpty();
            boolean hasVelocity = focusArea.getVelocity().getX() != 0 || focusArea.getVelocity().getY() != 0;
            if (paneNotTouched && hasVelocity && focusArea.isTranslatable()) {
                processPhysics(focusArea);
            }
        }
    }

    /**
     * Process the physics of a specific focus area.
     */
    private void processPhysics(FocusArea focusArea) {
        Point2D velocity = focusArea.getVelocity();
        Point2D delta = velocity.multiply(0.001 * PHYSICS_MILLISECOND_PERIOD);

        Point2D centre = PointUtils.getCentreOfPane(focusArea.getPane());

        if (centre.getX() + delta.getX() < 0) {
            delta = new Point2D(-centre.getX(), delta.getY());
            velocity = new Point2D(-velocity.getX() * COLLISION_VELOCITY_LOSS,
                    velocity.getY() * COLLISION_VELOCITY_LOSS);
        }

        if (centre.getY() + delta.getY() < 0) {
            delta = new Point2D(delta.getX(), -centre.getY());
            velocity = new Point2D(velocity.getX() * COLLISION_VELOCITY_LOSS,
                    -velocity.getY() * COLLISION_VELOCITY_LOSS);
        }

        if (centre.getX() + delta.getX() > rootPane.getWidth()) {
            delta = new Point2D( rootPane.getWidth() - centre.getX(), delta.getY());
            velocity = new Point2D(-velocity.getX() * COLLISION_VELOCITY_LOSS,
                    velocity.getY() * COLLISION_VELOCITY_LOSS);
        }

        if (centre.getY() + delta.getY() > rootPane.getHeight()) {
            delta = new Point2D(delta.getX(), rootPane.getHeight() - centre.getY());
            velocity = new Point2D(velocity.getX() * COLLISION_VELOCITY_LOSS,
                    -velocity.getY() * COLLISION_VELOCITY_LOSS);
        }

        focusArea.prependTransform(new Translate(delta.getX(), delta.getY()));

        velocity = velocity.multiply(1 - (1 - SURFACE_TENSION) * (0.001 * PHYSICS_MILLISECOND_PERIOD));
        if (PointUtils.distance(velocity, Point2D.ZERO) < 1) {
            velocity = Point2D.ZERO;
        }

        focusArea.setVelocity(velocity);
    }
}
