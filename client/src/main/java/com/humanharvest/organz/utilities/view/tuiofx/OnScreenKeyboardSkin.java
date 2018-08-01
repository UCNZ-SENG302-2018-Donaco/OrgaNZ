package com.humanharvest.organz.utilities.view.tuiofx;

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


public class OnScreenKeyboardSkin extends BehaviorSkinBase<OnScreenKeyboard, BehaviorBase<OnScreenKeyboard>> {
    private static Logger logger = LoggerFactory.getLogger(OnScreenKeyboardSkin.class);
    private OnScreenKeyboard onScreenKeyboard;
    private Node attachedNode;
    private Path layerPath;
    private double initScale = 0.0D;
    private Locale initLocal;
    private KeyboardPane keyboardPane;
    private TuioFXCanvas tuioFXCanvas;

    public OnScreenKeyboardSkin(final OnScreenKeyboard onScreenKeyboard) {
        super(onScreenKeyboard, new BehaviorBase(onScreenKeyboard, Collections.EMPTY_LIST));
        this.onScreenKeyboard = onScreenKeyboard;
        this.setupKeyboard();
        onScreenKeyboard.attachedNodeProperty().addListener(valueModel -> {
            if (OnScreenKeyboardSkin.this.tuioFXCanvas == null) {
                Parent root = OnScreenKeyboardSkin.this.getSkinnable().getAttachedNode().getScene().getRoot();
                if (!(root instanceof TuioFXCanvas)) {
                    OnScreenKeyboardSkin.logger.error("TuioFXCanvas is missing.");
                    return;
                }

                OnScreenKeyboardSkin.this.tuioFXCanvas = (TuioFXCanvas) root;
                OnScreenKeyboardSkin.this.tuioFXCanvas.getChildren().add(OnScreenKeyboardSkin.this.keyboardPane);
            }

            Node oldNode = OnScreenKeyboardSkin.this.attachedNode;
            OnScreenKeyboardSkin.this.attachedNode = onScreenKeyboard.getAttachedNode();
            OnScreenKeyboardSkin.this.keyboardPane.setAttachedNode(OnScreenKeyboardSkin.this.attachedNode);
            if (OnScreenKeyboardSkin.this.attachedNode != null) {
                if ((oldNode == null || oldNode.getScene() == null || !oldNode.equals(OnScreenKeyboardSkin.this.attachedNode)) && OnScreenKeyboardSkin.this.keyboardPane.isVisible()) {
                    OnScreenKeyboardSkin.this.keyboardPane.setVisible(false);
                }

                if (!OnScreenKeyboardSkin.this.keyboardPane.isVisible()) {
                    OnScreenKeyboardSkin.this.showKeyboard();
                }
            } else {
                OnScreenKeyboardSkin.this.hideKeyboard();
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

        this.keyboardPane.setOnKeyboardCloseButton(event -> OnScreenKeyboardSkin.this.hideKeyboard());
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
