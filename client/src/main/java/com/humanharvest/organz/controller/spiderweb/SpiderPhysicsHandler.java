package com.humanharvest.organz.controller.spiderweb;

import java.util.Objects;

import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.PhysicsHandler;

public class SpiderPhysicsHandler extends PhysicsHandler {

    public SpiderPhysicsHandler(Pane rootPane) {
        super(rootPane);
    }

    @Override
    public void processPhysics() {

        super.processPhysics();

        for (FocusArea focusArea : MultitouchHandler.getFocusAreas()) {
            boolean paneTouched = !MultitouchHandler.findPaneTouches(focusArea.getPane()).isEmpty();

            // Skip if touched or not moveable
            if (paneTouched || !focusArea.isTranslatable()) {
                continue;
            }

            Bounds bounds = focusArea.getPane().getBoundsInParent();

            for (FocusArea otherFocusArea : MultitouchHandler.getFocusAreas()) {

                // Skip if the same object
                if (Objects.equals(focusArea, otherFocusArea)) {
                    continue;
                }

                // If the other pane isn't moveable due to being touched or immobile
                boolean otherNotMoveable =
                        !MultitouchHandler.findPaneTouches(otherFocusArea.getPane()).isEmpty() || !focusArea.isTranslatable();
                if (otherNotMoveable){
                    continue;
                }

                Bounds otherBounds = otherFocusArea.getPane().getBoundsInParent();

                if (bounds.intersects(otherBounds)) {
                    System.out.println("COLLISION between " + focusArea + " and " + otherFocusArea);
                }
            }
        }
    }
}
