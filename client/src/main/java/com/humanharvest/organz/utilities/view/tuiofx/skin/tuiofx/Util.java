package com.humanharvest.organz.utilities.view.tuiofx.skin.tuiofx;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;
import org.tuiofx.internal.base.TuioFXCanvas;
import org.tuiofx.widgets.controls.KeyboardPane;

public class Util {
    public static double degreeDev = 13.0D;

    public Util() {
    }

    public static double getRotationAngleLocalToScene(Node node) {
        double xx = node.getLocalToSceneTransform().getMxx();
        double xy = node.getLocalToSceneTransform().getMxy();
        double yx = node.getLocalToSceneTransform().getMyx();
        double yy = node.getLocalToSceneTransform().getMyy();
        return Math.atan2(-xy, xx);
    }

    public static double getRotationDegreesLocalToScene(Node node) {
        double xx = node.getLocalToSceneTransform().getMxx();
        double xy = node.getLocalToSceneTransform().getMxy();
        double yx = node.getLocalToSceneTransform().getMyx();
        double yy = node.getLocalToSceneTransform().getMyy();
        return Math.toDegrees(Math.atan2(-xy, xx));
    }

    public static Point2D getTranslationLocalToScene(Node node) {
        double x = node.getLocalToSceneTransform().getTx();
        double y = node.getLocalToSceneTransform().getTy();
        return new Point2D(x, y);
    }

    public static double euclideanDistance(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static boolean isFirstQuadrant(double degree) {
        return -90.0D + degreeDev <= degree && degree <= 0.0D;
    }

    public static boolean isSecondQuadrant(double degree) {
        return 0.0D < degree && degree <= 90.0D - degreeDev;
    }

    public static boolean isThirdQuadrant(double degree) {
        return 90.0D + degreeDev <= degree && degree <= 180.0D;
    }

    public static boolean isFourthQuadrant(double degree) {
        return -180.0D <= degree && degree <= -90.0D - degreeDev;
    }

    public static boolean isBesideFirstAndFourthQuadrant(double degree) {
        return -90.0D - degreeDev <= degree && degree <= -90.0D + degreeDev;
    }

    public static boolean isBesideSecondAndThirdQuadrant(double degree) {
        return 90.0D - degreeDev <= degree && degree <= 90.0D + degreeDev;
    }

    public static double getDegreesBetweenTwoPoints(Point2D p1, Point2D p2) {
        double xDiff = p2.getX() - p1.getX();
        double yDiff = p2.getY() - p1.getY();
        return Math.toDegrees(Math.atan2(-yDiff, xDiff));
    }

    public static double getOffsetX(Object obj) {
        Scene scene;
        if (obj instanceof Node) {
            scene = ((Node) obj).getScene();
            return scene != null && scene.getWindow() != null ? scene.getX() + scene.getWindow().getX() : 0.0D;
        } else if (obj instanceof Scene) {
            scene = (Scene) obj;
            return scene.getWindow() != null ? scene.getX() + scene.getWindow().getX() : 0.0D;
        } else {
            return obj instanceof Window ? ((Window) obj).getX() : 0.0D;
        }
    }

    public static double getOffsetY(Object obj) {
        Scene scene;
        if (obj instanceof Node) {
            scene = ((Node) obj).getScene();
            return scene != null && scene.getWindow() != null ? scene.getY() + scene.getWindow().getY() : 0.0D;
        } else if (obj instanceof Scene) {
            scene = (Scene) obj;
            return scene.getWindow() != null ? scene.getY() + scene.getWindow().getY() : 0.0D;
        } else {
            return obj instanceof Window ? ((Window) obj).getY() : 0.0D;
        }
    }

    public static Bounds getBounds(Object obj) {
        if (obj instanceof Node) {
            Node n = (Node) obj;
            return n.localToScreen(n.getLayoutBounds());
        } else if (obj instanceof Window) {
            Window window = (Window) obj;
            return new BoundingBox(window.getX(), window.getY(), window.getWidth(), window.getHeight());
        } else {
            return new BoundingBox(0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    public static Node getFocusAreaStartingNode(Node node) {
        if (node == null) {
            return null;
        } else {
            Boolean focusArea = getFocusArea(node);
            if (focusArea) {
                return node;
            } else {
                Parent root = node.getScene().getRoot();
                Parent parent = node.getParent();
                Parent oldParent = null;
                Parent focusAreaNode = null;
                if (parent == null) {
                    return node instanceof Parent ? (Parent) node : null;
                } else {
                    while (parent != null) {
                        focusArea = getFocusArea(parent);
                        if (!parent.getParent().equals(root) && !(parent.getParent() instanceof BaseCanvas)) {
                            if (focusArea) {
                                return parent;
                            }
                            parent = parent.getParent();
                        } else {
                            focusAreaNode = parent;
                            parent = null;
                        }
                    }

                    return focusAreaNode;
                }
            }
        }
    }

    private static boolean getFocusArea(Node node) {
        String focusAreaString = (String) node.getProperties().getOrDefault("focusArea", "false");
        return Boolean.valueOf(focusAreaString);
    }

    public static Parent getNodeFirstParent(Node node) {
        if (node == null) {
            return null;
        } else {
            Parent root = node.getScene().getRoot();
            Parent parent = node.getParent();
            Parent oldParent = null;
            Parent firstParent = null;
            if (parent == null) {
                return node instanceof Parent ? (Parent) node : null;
            } else if (parent.getParent() == null) {
                return null;
            } else {
                while (parent != null) {
                    if (!parent.getParent().equals(root) && !(parent.getParent() instanceof TuioFXCanvas)) {
                        oldParent = parent;
                        parent = parent.getParent();
                    } else {
                        firstParent = parent instanceof KeyboardPane ? parent : oldParent;
                        parent = null;
                    }
                }

                return firstParent;
            }
        }
    }

    public static boolean isWithIn(double value, double lowerBound, double upperBound) {
        return value >= lowerBound && value <= upperBound;
    }
}
