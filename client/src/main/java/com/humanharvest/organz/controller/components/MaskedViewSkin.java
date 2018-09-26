package com.humanharvest.organz.controller.components;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class MaskedViewSkin extends SkinBase {

    private final Rectangle leftClip;
    private final Rectangle rightClip;
    private final Rectangle centerClip;

    private final StackPane stackPane;
    private final InvalidationListener translateXListener = it -> updateClip();
    private final WeakInvalidationListener weakTranslateXListener = new WeakInvalidationListener(translateXListener);

    public MaskedViewSkin(MaskedView view) {
        super(view);

        leftClip = new Rectangle();
        rightClip = new Rectangle();
        centerClip = new Rectangle();

        centerClip.setFill(Color.BLACK);

        leftClip.setManaged(false);
        centerClip.setManaged(false);
        rightClip.setManaged(false);

        Group group = new Group(leftClip, centerClip, rightClip);

        stackPane = new StackPane();
        stackPane.setManaged(false);
        stackPane.setClip(group);

        getChildren().add(stackPane);

        view.contentProperty().addListener((observable, oldContent, newContent) -> buildView(oldContent, newContent));
        buildView(null, view.getContent());

        view.widthProperty().addListener(it -> updateClip());

        view.fadingSizeProperty().addListener(it -> updateClip());
    }

    private void buildView(Node oldContent, Node newContent) {
        if (oldContent != null) {
            stackPane.getChildren().clear();
            oldContent.translateXProperty().removeListener(weakTranslateXListener);
        }

        if (newContent != null) {
            stackPane.getChildren().setAll(newContent);
            newContent.translateXProperty().addListener(weakTranslateXListener);
        }

        updateClip();
    }

    private void updateClip() {
        final MaskedView view = (MaskedView) getSkinnable();

        Node content = view.getContent();
        if (content != null) {

            final double fadingSize = view.getFadingSize();

            if (content.getTranslateX() < 0) {
                leftClip.setFill(new LinearGradient(0, 0, fadingSize, 0, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.RED),
                        new Stop(1, Color.BLACK)));
            } else {
                leftClip.setFill(Color.BLACK);
            }
            if (content.getTranslateX() + content.prefWidth(-1) > view.getWidth()) {
                rightClip.setFill(new LinearGradient(0, 0, fadingSize, 0, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.BLACK),
                        new Stop(1, Color.RED)));
            } else {
                rightClip.setFill(Color.BLACK);
            }
        }

        view.requestLayout();
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        final double fadingSize = Math.min(contentWidth / 2, ((MaskedView) getSkinnable()).getFadingSize());
        stackPane.resizeRelocate(snapPosition(contentX), snapPosition(contentY), snapSpace(contentWidth),
                snapSpace(contentHeight));
        resizeRelocate(leftClip, snapPosition(contentX), snapPosition(contentY), snapSpace(fadingSize),
                snapSpace(contentHeight));
        resizeRelocate(centerClip, snapPosition(contentX + fadingSize), snapPosition(contentY),
                snapSpace(contentWidth - 2 * fadingSize), snapSpace(contentHeight));
        resizeRelocate(rightClip, snapPosition(contentX + contentWidth - fadingSize), snapPosition(contentY),
                snapSpace(fadingSize), snapSpace(contentHeight));
    }

    private void resizeRelocate(Rectangle rect, double x, double y, double w, double h) {
        rect.setLayoutX(x);
        rect.setLayoutY(y);
        rect.setWidth(w);
        rect.setHeight(h);
    }
}