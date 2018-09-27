package com.humanharvest.organz.touch;

import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.controller.spiderweb.OrganWithRecipients;

public class OrganFocusArea extends FocusArea {

    private static final Logger LOGGER = Logger.getLogger(OrganFocusArea.class.getName());

    private static final double MAX_CLICK_DISTANCE = 25;
    private static final int MULTI_CLICK_INTERVAL =
            Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval") == null ? 200 :
                    (int) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");

    private final OrganWithRecipients organWithRecipients;
    private final Timer doubleClickTimer;
    private TimerTask currentClickTimer;

    private Point2D originalTouchPoint;
    private boolean countsAsClick;

    public OrganFocusArea(Pane pane, OrganWithRecipients organWithRecipients) {
        super(pane);
        this.organWithRecipients = organWithRecipients;
        doubleClickTimer = new Timer();
    }

    @Override
    protected void onTouchPressed(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchPressed(event, currentTouch);

        try {
            organWithRecipients.getOrganPane().toFront();
        } catch (RuntimeException e) {
            LOGGER.log(Level.WARNING, "Runtime exception when setting pane to front", e);
        }

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
            if (currentClickTimer != null) {
                currentClickTimer.cancel();
                currentClickTimer = null;
                organWithRecipients.handleOrganDoubleClick();
            } else {
                currentClickTimer = new TimerTask() {
                    @Override
                    public void run() {
                        organWithRecipients.handleOrganSingleClick();
                        currentClickTimer = null;
                    }
                };
                doubleClickTimer.schedule(currentClickTimer, MULTI_CLICK_INTERVAL);
            }
        } else if (currentClickTimer != null) {
            currentClickTimer.cancel();
            currentClickTimer = null;
            organWithRecipients.handleOrganSingleClick();
        }

        organWithRecipients.handleTouchReleased();
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
