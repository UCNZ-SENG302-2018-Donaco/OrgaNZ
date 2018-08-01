package com.humanharvest.organz.utilities.view.tuiofx.skin;


import com.sun.javafx.scene.control.skin.TextAreaSkin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tuiofx.widgets.utils.Util;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public class TextAreaSkinAndroid extends TextAreaSkin {
    private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");
    private final String kbGroupName;
    private final boolean useFocusArea;
    protected ObservableBooleanValue caretVisible;
    private BooleanProperty blink = new SimpleBooleanProperty(this, "blink", true);
    private TextAreaSkinAndroid.CaretBlinking caretBlinking;
    private BooleanProperty tfFocused;

    public TextAreaSkinAndroid(final TextArea textInput) {
        super(textInput);
        this.caretBlinking = new TextAreaSkinAndroid.CaretBlinking(this.blink);
        this.tfFocused = new BooleanPropertyBase(false) {
            protected void invalidated() {
                TextAreaSkinAndroid.this.pseudoClassStateChanged(TextAreaSkinAndroid.FOCUSED_PSEUDO_CLASS, this.get());
            }

            public Object getBean() {
                return TextAreaSkinAndroid.this;
            }

            public String getName() {
                return "focused";
            }
        };
        KeyboardManager kbManager = KeyboardManager.getInstance();
        this.kbGroupName = kbManager.getNodeKbGroup(textInput);
        Node focusAreaNode = Util.getFocusAreaStartingNode(textInput);
        this.useFocusArea = kbManager.isUsingFocusAreaProp(textInput);
        Function<Node, TextField> isTargetTextField = (target) -> null;
        final OnScreenKeyboard keyboard;
        if (this.useFocusArea) {
            keyboard = KeyboardManager.getInstance().getKeyboard(focusAreaNode);
            focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
                if (!event.isSynthesized()) {
                    this.detachKeyboard(keyboard, event.getTarget());
                }

            });
            focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, (event) -> this.detachKeyboard(keyboard, event.getTarget()));
        } else if (this.kbGroupName == null) {
            keyboard = KeyboardManager.getInstance().getKeyboard(textInput);
        } else {
            keyboard = KeyboardManager.getInstance().getKeyboard(this.kbGroupName);
        }

        textInput.focusedProperty().addListener((observable, wasFocused, isFocused) -> TextAreaSkinAndroid.this.handleFocus(textInput, keyboard));
        keyboard.attachedNodeProperty().addListener((observable, oldValue, newValue) -> {
            if (!keyboard.isShowing() && keyboard.getAttachedNode() == null) {
                textInput.selectRange(textInput.getLength(), textInput.getLength());
                System.out.println("SET FALSE " + newValue);
                TextAreaSkinAndroid.this.tfFocused.set(false);
                if (textInput.isFocused()) {
                    textInput.getParent().requestFocus();
                }
            }

        });
        this.caretVisible = new BooleanBinding() {
            {
                this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), textInput.editableProperty(), TextAreaSkinAndroid.this.displayCaret, TextAreaSkinAndroid.this.blink, keyboard.attachedNodeProperty());
            }

            protected boolean computeValue() {
                return !TextAreaSkinAndroid.this.blink.get() && keyboard.getAttachedNode() != null && TextAreaSkinAndroid.this.displayCaret.get() && keyboard.isShowing() && keyboard.getAttachedNode().equals(textInput) && !textInput.isDisabled() && textInput.isEditable();
            }
        };
        this.caretPath.opacityProperty().bind(new DoubleBinding() {
            {
                this.bind(TextAreaSkinAndroid.this.caretVisible);
            }

            protected double computeValue() {
                return TextAreaSkinAndroid.this.caretVisible.get() ? 1.0D : 0.0D;
            }
        });
        if (SHOW_HANDLES) {
            this.caretHandle.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), textInput.editableProperty(), textInput.lengthProperty(), TextAreaSkinAndroid.this.displayCaret, keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return keyboard.getAttachedNode() != null && TextAreaSkinAndroid.this.displayCaret.get() && keyboard.isShowing() && keyboard.getAttachedNode().equals(textInput) && textInput.getCaretPosition() == textInput.getAnchor() && !textInput.isDisabled() && textInput.isEditable() && textInput.getLength() > 0;
                }
            });
            this.selectionHandle1.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), TextAreaSkinAndroid.this.displayCaret, keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return keyboard.getAttachedNode() != null && TextAreaSkinAndroid.this.displayCaret.get() && textInput.getCaretPosition() != textInput.getAnchor() && !textInput.isDisabled();
                }
            });
            this.selectionHandle2.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), TextAreaSkinAndroid.this.displayCaret, keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return keyboard.getAttachedNode() != null && TextAreaSkinAndroid.this.displayCaret.get() && textInput.getCaretPosition() != textInput.getAnchor() && !textInput.isDisabled();
                }
            });
        }

        if (textInput.isFocused() && keyboard.getAttachedNode() == null) {
            textInput.skinProperty().set(this);
            this.handleFocus(textInput, keyboard);
        }

        textInput.sceneProperty().addListener(observable -> {
            if (!textInput.isFocused() && keyboard.isShowing() && keyboard.getAttachedNode() != null && keyboard.getAttachedNode().equals(textInput)) {
                keyboard.detach();
                TextAreaSkinAndroid.this.tfFocused.set(false);
            }

        });
    }

    private void detachKeyboard(OnScreenKeyboard keyboard, EventTarget targetNode) {
        Parent parent = ((Node) targetNode).getParent();
        Node hitTextArea = null;
        if (targetNode instanceof Text) {
            hitTextArea = parent.getParent().getParent().getParent().getParent().getParent();
        } else if (!(targetNode instanceof TextArea) && targetNode instanceof Region) {
            hitTextArea = parent.getParent().getParent().getParent();
        } else if (targetNode instanceof ScrollPane) {
            hitTextArea = parent;
        }

        if (hitTextArea == null || !hitTextArea.equals(keyboard.getAttachedNode())) {
            if (!targetNode.equals(keyboard.getAttachedNode()) && !targetNode.equals(this)) {
                keyboard.detach();
            }
        }
    }

    private void handleFocus(TextArea textInput, OnScreenKeyboard keyboard) {
        if (textInput.isEditable() && textInput.isFocused() && !textInput.isDisabled()) {
            this.setCaretAnimating(true);
            keyboard.attach(textInput);
        } else if (!textInput.isFocused() && keyboard.isShowing() && keyboard.getAttachedNode() != null && keyboard.getAttachedNode().equals(textInput)) {
            this.setCaretAnimating(true);
            this.tfFocused.set(false);
            this.tfFocused.set(true);
        }

    }

    public void setCaretAnimating(boolean value) {
        if (this.caretBlinking != null) {
            if (value) {
                this.caretBlinking.start();
            } else {
                this.caretBlinking.stop();
                this.blink.set(true);
            }

        }
    }

    public void populateContextMenu(ContextMenu contextMenu) {
        super.populateContextMenu(contextMenu);
        contextMenu.setAutoHide(false);
    }

    private static final class CaretBlinking {
        private final Timeline caretTimeline;
        private final WeakReference<BooleanProperty> blinkPropertyRef;

        public CaretBlinking(BooleanProperty blinkProperty) {
            this.blinkPropertyRef = new WeakReference(blinkProperty);
            this.caretTimeline = new Timeline();
            this.caretTimeline.setCycleCount(-1);
            this.caretTimeline.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, (event) -> this.setBlink(false)), new KeyFrame(Duration.seconds(0.5D), (event) -> this.setBlink(true)), new KeyFrame(Duration.seconds(1.0D)));
        }

        public void start() {
            this.caretTimeline.play();
        }

        public void stop() {
            this.caretTimeline.stop();
        }

        private void setBlink(boolean value) {
            BooleanProperty blinkProperty = this.blinkPropertyRef.get();
            if (blinkProperty == null) {
                this.caretTimeline.stop();
            } else {
                blinkProperty.set(value);
            }
        }
    }
}
