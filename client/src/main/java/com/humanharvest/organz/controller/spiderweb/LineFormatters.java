package com.humanharvest.organz.controller.spiderweb;

import javafx.geometry.Bounds;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.controller.components.ExpiryBarUtils;
import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;

public class LineFormatters {

    private static final ColorAdjust OVERRIDDEN_COLOR = new ColorAdjust(0, 0, -0.6, -0.6);
    private static final ColorAdjust EXPIRED_COLOR = new ColorAdjust(0, 0, -0.4, -0.4);
    private static final double LABEL_OFFSET = 50.0;
    private static final DurationFormat durationFormat = DurationFormat.X_HRS_Y_MINS_SECS;

    public static void updateDonorConnector(DonatedOrgan donatedOrgan, Line line, Pane organPane) {
        OrganState organState = donatedOrgan.getState();
        switch (organState) {
            case OVERRIDDEN:
                line.setStroke(Color.BLACK);
                organPane.setEffect(OVERRIDDEN_COLOR);
                break;
            case EXPIRED:
                line.setStroke(ExpiryBarUtils.darkGreyColour);
                organPane.setEffect(EXPIRED_COLOR);
                break;
            case NO_EXPIRY:
                line.setStroke(ExpiryBarUtils.noExpiryGreenColour);
                organPane.setEffect(null);
                break;
            case CURRENT:
                LinearGradient linearGradient = ExpiryBarUtils.getLinearGradient(
                        donatedOrgan.getProgressDecimal(), donatedOrgan.getFullMarker(),
                        line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                line.setStroke(linearGradient);
                organPane.setEffect(null);
                break;
            case TRANSPLANT_COMPLETED:
                //TODO
                break;
        }
    }

    public static void updateConnectorText(Text durationText, DonatedOrgan donatedOrgan, Line line) {
        // Set the text
        durationText.setText(ExpiryBarUtils.getDurationString(donatedOrgan, durationFormat));

        // Remove the old translation
        durationText.getTransforms().removeIf(Affine.class::isInstance);

        Affine trans = new Affine();

        // Translate the text to the left by at least LABEL_OFFSET and more if the line is a large width
        // Also move it up by 5 pixels to put it just above the line
        double xWidth = line.getStartX() - line.getEndX();
        double yWidth = line.getStartY() - line.getEndY();
        double lineWidth = Math.sqrt(Math.pow(xWidth, 2) + Math.pow(yWidth, 2));
        trans.prepend(new Translate(Math.max(LABEL_OFFSET, lineWidth * 0.2), -5));

        // Rotate the text by the angle of the line
        double angle = getAngle(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        trans.prepend(new Rotate(angle));

        // Apply the new transformation
        durationText.getTransforms().add(trans);

        // Translate the text to the end of the line (then the above transforms take effect)
        durationText.setTranslateX(line.getEndX());
        durationText.setTranslateY(line.getEndY());
    }

    public static void updateRecipientConnector(DonatedOrgan donatedOrgan, Line line) {
        OrganState organState = donatedOrgan.getState();
        switch (organState) {
            case OVERRIDDEN:
                line.setStroke(Color.BLACK);
                break;
            case EXPIRED:
                line.setStroke(ExpiryBarUtils.darkGreyColour);
                break;
            case NO_EXPIRY:
                line.setStroke(ExpiryBarUtils.noExpiryGreenColour);
                break;
            case CURRENT:
                line.setStroke(Color.WHITE);
                break;
            case TRANSPLANT_COMPLETED:
                //TODO
                break;
        }
    }

    public static void updateMatchesListPosition(Pane matchesPane, Transform newTransform) {
        FocusArea focusArea = (FocusArea) matchesPane.getUserData();

        // If not currently touching the matches pane
        if (MultitouchHandler.findPaneTouches(matchesPane).isEmpty()) {
            Affine transform = new Affine();
            transform.prepend(new Scale(0.5, 0.5));
            transform.prepend(new Translate(-50, 90));
            transform.prepend(newTransform);
            focusArea.setTransform(transform);
        }
    }

    public static void updateOrganPanePosition(Pane organPane, Transform newTransform) {
        FocusArea focusArea = (FocusArea) organPane.getUserData();

        // If not currently touching the organ pane
        if (MultitouchHandler.findPaneTouches(organPane).isEmpty()) {
            Affine transform = new Affine();
            transform.prepend(new Translate(50, -90));
            transform.prepend(newTransform);
            focusArea.setTransform(transform);
        }
    }

    private static double getAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(y1 - y2, x1 - x2));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

}
