package com.humanharvest.organz.skin;

import javafx.scene.input.TouchEvent;

public class ClickHelper {

    private int clickCount;
    private double clickX = Double.MIN_VALUE;
    private double clickY = Double.MIN_VALUE;
    private long lastClickTime;

    public void calculateClickCount(TouchEvent event) {

        long clickTime = System.nanoTime();
        double deltaX = Math.abs(event.getTouchPoint().getSceneX() - clickX);
        double deltaY = Math.abs(event.getTouchPoint().getSceneY() - clickY);

        if (clickTime - lastClickTime >= 250_000_000) { // 0.25s
            clickCount = 1;
        } else if(deltaX > 10 || deltaY > 10) {
            clickCount = 1;
        } else {
            clickCount++;
        }

        clickX = event.getTouchPoint().getSceneX();
        clickY = event.getTouchPoint().getSceneY();
        lastClickTime = System.nanoTime();
    }

    public int getClickCount() {
        return clickCount;
    }
}
