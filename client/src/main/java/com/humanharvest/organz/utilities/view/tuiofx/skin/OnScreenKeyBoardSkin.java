package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.transform.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tuiofx.internal.base.TuioFXCanvas;
import org.tuiofx.widgets.controls.KeyboardPane;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;

public class OnScreenKeyBoardSkin extends BehaviorSkinBase<OnScreenKeyboard, BehaviorBase<OnScreenKeyboard>> {
    private static Logger logger = LoggerFactory.getLogger(OnScreenKeyBoardSkin.class);
    OnScreenKeyboard onScreenKeyboard;
    private Node attachedNode;
    private Path layerPath;
    private double initScale = 0.0D;
    private Locale initLocal;
    private KeyboardPane keyboardPane;
    private TuioFXCanvas tuioFXCanvas;

    public OnScreenKeyBoardSkin(final OnScreenKeyboard onScreenKeyboard) {
        super(onScreenKeyboard, new BehaviorBase(onScreenKeyboard, Collections.EMPTY_LIST));
        this.onScreenKeyboard = onScreenKeyboard;
        this.setupKeyboard();
        onScreenKeyboard.attachedNodeProperty().addListener(valueModel -> {
            if (OnScreenKeyBoardSkin.this.tuioFXCanvas == null) {
                Parent root = OnScreenKeyBoardSkin.this.getSkinnable().getAttachedNode().getScene().getRoot();
                if (!(root instanceof TuioFXCanvas)) {
                    OnScreenKeyBoardSkin.logger.error("TuioFXCanvas is missing.");
                    return;
                }

                OnScreenKeyBoardSkin.this.tuioFXCanvas = (TuioFXCanvas) root;
                OnScreenKeyBoardSkin.this.tuioFXCanvas.getChildren().add(OnScreenKeyBoardSkin.this.keyboardPane);
            }

            Node oldNode = OnScreenKeyBoardSkin.this.attachedNode;
            OnScreenKeyBoardSkin.this.attachedNode = onScreenKeyboard.getAttachedNode();
            OnScreenKeyBoardSkin.this.keyboardPane.setAttachedNode(OnScreenKeyBoardSkin.this.attachedNode);
            if (OnScreenKeyBoardSkin.this.attachedNode != null) {
                if ((oldNode == null || oldNode.getScene() == null || !oldNode.equals(OnScreenKeyBoardSkin.this.attachedNode)) && OnScreenKeyBoardSkin.this.keyboardPane.isVisible()) {
                    OnScreenKeyBoardSkin.this.keyboardPane.setVisible(false);
                }

                if (!OnScreenKeyBoardSkin.this.keyboardPane.isVisible()) {
                    OnScreenKeyBoardSkin.this.showKeyboard();
                }
            } else {
                OnScreenKeyBoardSkin.this.hideKeyboard();
            }

        });
    }

    private void setupKeyboard() {
        this.onScreenKeyboard.setFocusTraversable(false);
        this.onScreenKeyboard.setVisible(false);
        this.buildKeyboard();
    }

    private void buildKeyboard() {
        this.initScale = 1.0D;
        this.initLocal = Locale.ENGLISH;
        this.keyboardPane = new KeyboardPane(this.layerPath, this.initLocal);
        if (this.initScale > 0.0D) {
            this.keyboardPane.setScale(this.initScale);
        }

        this.keyboardPane.setOnKeyboardCloseButton(event -> OnScreenKeyBoardSkin.this.hideKeyboard());
    }

    private void showKeyboard() {
        this.keyboardPane.setVisible(true);
        this.keyboardPane.getTransforms().clear();
        this.keyboardPane.getTransforms().addAll(this.attachedNode.getLocalToSceneTransform(), new Translate(0.0D, 50.0D));
    }

    private void hideKeyboard() {
        this.keyboardPane.setVisible(false);
        this.onScreenKeyboard.detach();
    }

    public boolean isVisible() {
        return this.keyboardPane.isVisible();
    }
}
