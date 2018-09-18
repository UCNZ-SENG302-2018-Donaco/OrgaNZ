package com.humanharvest.organz.touch;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Translate;

public final class PhysicsHelper {

    public static final double MIN_VELOCITY_THRESHOLD = 10;
    private static final double COLLISION_VELOCITY_LOSS = 0.5;
    private static final double SURFACE_TENSION = 0.2;
    private static final long PHYSICS_MILLISECOND_PERIOD = 16;

    private static Timer physicsTimer;
    private static Pane rootPane;

    private PhysicsHelper() {
    }

    public static void initialise(Pane newRootPane) {

        rootPane = newRootPane;

        physicsTimer = new Timer();
        physicsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(PhysicsHelper::processPhysics);
            }
        }, 0, PHYSICS_MILLISECOND_PERIOD);
    }

    /**
     * Processes physics for the focus areas.
     */
    private static void processPhysics() {

        for (FocusArea focusArea : MultitouchHandler.getFocusAreas()) {
            boolean paneNotTouched = MultitouchHandler.findPaneTouches(focusArea.getPane()).isEmpty();
            boolean hasVelocity = focusArea.getVelocity().getX() != 0 || focusArea.getVelocity().getY() != 0;
            if (paneNotTouched && hasVelocity && focusArea.isTranslatable()) {
                processPhysics(focusArea);
            }

            if (paneNotTouched && focusArea.isTranslatable()) {
                Bounds bounds = focusArea.getPane().getBoundsInParent();

                for (FocusArea otherFocusArea : MultitouchHandler.getFocusAreas()) {

                    if (Objects.equals(focusArea, otherFocusArea)) {
                        continue;
                    }

                    Bounds otherBounds = otherFocusArea.getPane().getBoundsInParent();

                    if (bounds.contains(otherBounds)) {
                        System.out.println("COLLISION");
                    }

                    boolean otherNotTouched = MultitouchHandler.findPaneTouches(otherFocusArea.getPane()).isEmpty();
                }
            }
        }
    }

    private static void processGravity() {

        for (FocusArea focusArea : MultitouchHandler.getFocusAreas()) {
            double mass1 = focusArea.isTranslatable() ? 10 : 1000;
            Point2D point1 = PointUtils.getCentreOfPane(focusArea.getPane());

            for (FocusArea otherFocusArea : MultitouchHandler.getFocusAreas()) {
                if (Objects.equals(otherFocusArea, focusArea)) {
                    continue;
                }

                double mass2 = otherFocusArea.isTranslatable() ? 10 : 1000;
                Point2D point2 = PointUtils.getCentreOfPane(otherFocusArea.getPane());

                double distance = PointUtils.distance(point1, point2);

                double force = mass1 * mass2 / (distance * distance);
                double forceTick = force / 1000 * PHYSICS_MILLISECOND_PERIOD;

                Point2D direction = point2.subtract(point1).normalize();

                focusArea.addVelocity(direction.multiply(forceTick));
                otherFocusArea.addVelocity(Point2D.ZERO.subtract(direction).multiply(forceTick));
            }
        }
    }

    /**
     * Process the physics of a specific focus area.
     */
    private static void processPhysics(FocusArea focusArea) {
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

    public static void stop() {
        physicsTimer.cancel();
    }
}
