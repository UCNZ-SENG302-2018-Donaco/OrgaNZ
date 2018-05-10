package seng302.UI.Validation;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

public class UIValidation {

    private final Collection<Watched> components = new ArrayList<>();
    private final Collection<Control> disables = new ArrayList<>();

    public UIValidation add(TextField textField, Validator validator) {
        Watched watched = new TextWatched(textField, validator);
        components.add(watched);
        textField.textProperty().addListener(observable -> {
            if (watched.isValid()) {
                watched.setValid();

                if (!disables.isEmpty()) {
                    if (isAllValid()) {
                        enableAll();
                    } else {
                        disableAll();
                    }
                }
            } else {
                watched.setInvalid();
                disableAll();
            }
        });
        return this;
    }

    private boolean isAllValid() {
        for (Watched watched : components) {
            if (!watched.isValid()) {
                return false;
            }
        }

        return true;
    }

    private void disableAll() {
        for (Control control : disables) {
            control.setDisable(true);
        }
    }

    private void enableAll() {
        for (Control control : disables) {
            control.setDisable(false);
        }
    }

    public UIValidation addDisableButton(Control control) {
        disables.add(control);
        return this;
    }

    private abstract static class Watched {
        private final Validator validator;
        private final Control control;

        Watched(Control control, Validator validator) {
            this.control = control;
            this.validator = validator;

            this.control.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != newValue) {
                    ObservableList<String> styleClasses = control.getStyleClass();
                    if (newValue) {
                        if (styleClasses.contains("invalid")) {
                            PopOver popOver = (PopOver)control.getUserData();
                            popOver.show(control);
                        }
                    } else if (!control.isHover()) {
                        if (styleClasses.contains("invalid")) {
                            PopOver popOver = (PopOver)control.getUserData();
                            popOver.hide();
                        }
                    }
                }
            });

            this.control.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue != newValue) {
                    ObservableList<String> styleClasses = control.getStyleClass();
                    if (newValue) {
                        if (styleClasses.contains("invalid")) {
                            PopOver popOver = (PopOver)control.getUserData();
                            popOver.show(control);
                        }
                    } else if (!control.isFocused()) {
                        if (styleClasses.contains("invalid")) {
                            PopOver popOver = (PopOver)control.getUserData();
                            popOver.hide();
                        }
                    }
                }
            });
        }

        public abstract boolean isValid();

        void setInvalid() {
            ObservableList<String> styleClasses = control.getStyleClass();
            if (!styleClasses.contains("invalid")) {
                styleClasses.add("invalid");

                PopOver popOver = (PopOver)control.getUserData();
                if (popOver == null) {
                    Label label = new Label(validator.getErrorMessage());
                    popOver = new PopOver(label);
                    popOver.setAutoHide(false);
                    popOver.setArrowLocation(ArrowLocation.TOP_LEFT);
                    control.setUserData(popOver);
                }
                popOver.show(control);
            }
            control.applyCss();
        }

        void setValid() {
            ObservableList<String> styleClasses = control.getStyleClass();
            if (styleClasses.contains("invalid")) {
                styleClasses.remove("invalid");

                PopOver popOver = (PopOver)control.getUserData();
                popOver.hide();
            }
        }

        Validator getValidator() {
            return validator;
        }
    }

    private static class TextWatched extends Watched {

        private final TextField textField;

        TextWatched(TextField textField, Validator validator) {
            super(textField, validator);
            this.textField = textField;
        }

        @Override
        public boolean isValid() {
            return getValidator().isValid(textField.getText());
        }
    }
}
