package com.humanharvest.organz.utilities.view.tuiofx.skin.tuiofx;

import javafx.scene.layout.Pane;

public class BaseCanvas extends Pane {

    public BaseCanvas() {
    }

    protected void layoutChildren() {
    }

    public void resize(double width, double height) {
        super.resize(width, height);
        if (this.getChildren().size() > 0) {
            this.getChildren().get(0).resize(width, height);
        }

    }
}