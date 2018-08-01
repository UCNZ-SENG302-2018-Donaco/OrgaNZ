package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.skin.ScrollPaneSkin;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import org.tuiofx.internal.util.Util;

public class MTScrollPaneSkin extends ScrollPaneSkin {
    public MTScrollPaneSkin(ScrollPane scrollpane) {
        super(scrollpane);
        this.getSkinnable().addEventFilter(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            boolean isEventCopyFired = false;

            public void handle(ScrollEvent event) {
                if (!this.isEventCopyFired) {
                    this.isEventCopyFired = true;
                    double angle = Util.getRotationDegreesLocalToScene(MTScrollPaneSkin.this.getSkinnable());
                    Rotate r = new Rotate(-angle);
                    Point2D transformed = r.transform(event.getDeltaX(), event.getDeltaY());
                    double deltaX = transformed.getX();
                    double deltaY = transformed.getY();
                    ScrollEvent evt = new ScrollEvent(event.getEventType(), event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), event.isShiftDown(), event.isControlDown(), event.isAltDown(), event.isMetaDown(), event.isDirect(), event.isInertia(), deltaX, deltaY, event.getTotalDeltaX(), event.getTotalDeltaY(), event.getTextDeltaXUnits(), event.getTextDeltaX(), event.getTextDeltaYUnits(), event.getTextDeltaY(), event.getTouchCount(), event.getPickResult());
                    Event.fireEvent(event.getTarget(), evt);
                    event.consume();
                    this.isEventCopyFired = false;
                }

            }
        });
    }
}
