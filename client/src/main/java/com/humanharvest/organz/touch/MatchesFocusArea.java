package com.humanharvest.organz.touch;

import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.controller.spiderweb.OrganWithRecipients;

public class MatchesFocusArea extends FocusArea {

    private final OrganWithRecipients organWithRecipients;

    public MatchesFocusArea(Pane pane, OrganWithRecipients organWithRecipients) {
        super(pane);
        this.organWithRecipients = organWithRecipients;
    }

    @Override
    protected void onTouchReleased(TouchEvent event, CurrentTouch currentTouch) {
        super.onTouchReleased(event, currentTouch);
        organWithRecipients.handleTouchReleased();
    }
}
