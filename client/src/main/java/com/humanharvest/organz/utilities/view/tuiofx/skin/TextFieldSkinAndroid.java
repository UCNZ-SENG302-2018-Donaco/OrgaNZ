package com.humanharvest.organz.utilities.view.tuiofx.skin;

import com.humanharvest.organz.utilities.view.tuiofx.skin.tuiofx.Util;
import com.sun.javafx.scene.control.skin.TextFieldSkin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.css.PseudoClass;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.tuiofx.widgets.behavior.MTPasswordFieldBehavior;
import org.tuiofx.widgets.behavior.MTTextFieldBehavior;

import java.lang.ref.WeakReference;
import java.util.Iterator;

public class TextFieldSkinAndroid extends TextFieldSkin {
    private static final PseudoClass FOCUSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focused");
    private final Boolean useFocusArea;
    private final String kbGroupName;
    private final TextField textInput;
    private final OnScreenKeyboard keyboard;
    private final TextFieldSkinAndroid.CaretBlinking caretBlinking;
    protected ObservableBooleanValue caretVisible;
    private BooleanProperty blink = new SimpleBooleanProperty(this, "blink", true);
    private BooleanProperty tfFocused;

    public TextFieldSkinAndroid(final TextField textInput) {
        super(textInput, textInput instanceof PasswordField ? new MTPasswordFieldBehavior((PasswordField) textInput) : new MTTextFieldBehavior(textInput));
        this.caretBlinking = new TextFieldSkinAndroid.CaretBlinking(this.blink);
        this.tfFocused = new BooleanPropertyBase(false) {
            protected void invalidated() {
                TextFieldSkinAndroid.this.pseudoClassStateChanged(TextFieldSkinAndroid.FOCUSED_PSEUDO_CLASS, this.get());
            }

            public Object getBean() {
                return TextFieldSkinAndroid.this;
            }

            public String getName() {
                return "focused";
            }
        };
        this.textInput = textInput;
        KeyboardManager kbManager = KeyboardManager.getInstance();
        this.kbGroupName = kbManager.getNodeKbGroup(textInput);
        Node focusAreaNode = Util.getFocusAreaStartingNode(textInput);
        this.useFocusArea = kbManager.isUsingFocusAreaProp(textInput);
        if (this.useFocusArea) {
            this.keyboard = KeyboardManager.getInstance().getKeyboard(focusAreaNode);
            focusAreaNode.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
                if (!event.isSynthesized()) {
                    this.detachKeyboard(this.keyboard, event.getTarget());
                }

            });
            focusAreaNode.addEventFilter(TouchEvent.TOUCH_PRESSED, (event) -> this.detachKeyboard(this.keyboard, event.getTarget()));
        } else if (this.kbGroupName == null) {
            this.keyboard = KeyboardManager.getInstance().getKeyboard(textInput);
        } else {
            this.keyboard = KeyboardManager.getInstance().getKeyboard(this.kbGroupName);
        }

        textInput.focusedProperty().addListener((observable, wasFocused, isFocused) -> this.handleFocus(textInput, this.keyboard));
        this.keyboard.attachedNodeProperty().addListener((observable, oldValue, newValue) -> {
            if (!TextFieldSkinAndroid.this.keyboard.isShowing() && TextFieldSkinAndroid.this.keyboard.getAttachedNode() == null) {
                textInput.selectRange(textInput.getLength(), textInput.getLength());
                TextFieldSkinAndroid.this.tfFocused.set(false);
                if (textInput.isFocused()) {
                    textInput.getParent().requestFocus();
                }
            }

        });
        this.caretVisible = new BooleanBinding() {
            {
                this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), textInput.editableProperty(), TextFieldSkinAndroid.this.displayCaret, TextFieldSkinAndroid.this.blink, TextFieldSkinAndroid.this.keyboard.attachedNodeProperty());
            }

            protected boolean computeValue() {
                return !TextFieldSkinAndroid.this.blink.get() && TextFieldSkinAndroid.this.keyboard.getAttachedNode() != null && TextFieldSkinAndroid.this.displayCaret.get() && TextFieldSkinAndroid.this.keyboard.isShowing() && TextFieldSkinAndroid.this.keyboard.getAttachedNode().equals(textInput) && !textInput.isDisabled() && textInput.isEditable();
            }
        };
        this.caretPath.opacityProperty().bind(new DoubleBinding() {
            {
                this.bind(TextFieldSkinAndroid.this.caretVisible);
            }

            protected double computeValue() {
                return TextFieldSkinAndroid.this.caretVisible.get() ? 1.0D : 0.0D;
            }
        });
        if (SHOW_HANDLES) {
            this.caretHandle.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), textInput.editableProperty(), textInput.lengthProperty(), TextFieldSkinAndroid.this.displayCaret, TextFieldSkinAndroid.this.keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return TextFieldSkinAndroid.this.keyboard.getAttachedNode() != null && TextFieldSkinAndroid.this.displayCaret.get() && TextFieldSkinAndroid.this.keyboard.isShowing() && TextFieldSkinAndroid.this.keyboard.getAttachedNode().equals(textInput) && textInput.getCaretPosition() == textInput.getAnchor() && !textInput.isDisabled() && textInput.isEditable() && textInput.getLength() > 0;
                }
            });
            this.selectionHandle1.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), TextFieldSkinAndroid.this.displayCaret, TextFieldSkinAndroid.this.keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return TextFieldSkinAndroid.this.keyboard.getAttachedNode() != null && TextFieldSkinAndroid.this.displayCaret.get() && textInput.getCaretPosition() != textInput.getAnchor() && !textInput.isDisabled();
                }
            });
            this.selectionHandle2.visibleProperty().bind(new BooleanBinding() {
                {
                    this.bind(textInput.focusedProperty(), textInput.anchorProperty(), textInput.caretPositionProperty(), textInput.disabledProperty(), TextFieldSkinAndroid.this.displayCaret, TextFieldSkinAndroid.this.keyboard.attachedNodeProperty());
                }

                protected boolean computeValue() {
                    return TextFieldSkinAndroid.this.keyboard.getAttachedNode() != null && TextFieldSkinAndroid.this.displayCaret.get() && textInput.getCaretPosition() != textInput.getAnchor() && !textInput.isDisabled();
                }
            });
        }

        Iterator var4 = this.getChildren().iterator();

        while (true) {
            Node node;
            do {
                if (!var4.hasNext()) {
                    if (textInput.isFocused() && this.keyboard.getAttachedNode() == null) {
                        textInput.skinProperty().set(this);
                        this.handleFocus(textInput, this.keyboard);
                    }

                    textInput.sceneProperty().addListener(observable -> {
                        Scene scene = (Scene) ((ReadOnlyObjectProperty) observable).getValue();
                        if (scene == null && TextFieldSkinAndroid.this.keyboard.getAttachedNode() != null && TextFieldSkinAndroid.this.keyboard.getAttachedNode().equals(textInput)) {
                            TextFieldSkinAndroid.this.keyboard.detach();
                            TextFieldSkinAndroid.this.tfFocused.set(false);
                        }

                    });
                    return;
                }

                node = (Node) var4.next();
            } while (!(node instanceof Pane));

            Pane textGroup = (Pane) node;

            for (Node child : textGroup.getChildren()) {
                if (child instanceof Path) {
                    Path selectionHighlightPath = (Path) child;
                    selectionHighlightPath.visibleProperty().bind(textInput.anchorProperty().isNotEqualTo(textInput.caretPositionProperty()));
                }
            }
        }
    }

    private void detachKeyboard(OnScreenKeyboard keyboard, EventTarget targetNode) {
        Parent parent = ((Node) targetNode).getParent();
        Node hitTextArea = null;
        if (targetNode instanceof Text) {
            hitTextArea = parent.getParent();
        } else if (!(targetNode instanceof TextField) && targetNode instanceof Pane) {
            hitTextArea = parent;
        }

        if (hitTextArea == null || !hitTextArea.equals(keyboard.getAttachedNode())) {
            if (!targetNode.equals(keyboard.getAttachedNode()) && !targetNode.equals(this)) {
                keyboard.detach();
            }
        }
    }

    private void handleFocus(TextField textInput, OnScreenKeyboard keyboard) {
        if (textInput.isEditable() && textInput.isFocused()) {
            this.setCaretAnimating(true);
            keyboard.attach(textInput);
        } else if (!textInput.isFocused() && keyboard.isShowing() && keyboard.getAttachedNode() != null && keyboard.getAttachedNode().equals(textInput)) {
            this.setCaretAnimating(true);
            this.tfFocused.set(false);
            this.tfFocused.set(true);
        }

    }

    public boolean isMultiTouchFocused() {
        return !this.textInput.isFocused() && this.keyboard.isShowing() && this.keyboard.getAttachedNode() != null && this.keyboard.getAttachedNode().equals(this.textInput);
    }

    public void detachKeyboard() {
        if (this.keyboard != null) {
            this.keyboard.detach();
            this.tfFocused.set(false);
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

    private final class CaretBlinking {
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
