package seng302.UI.Watchers;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class UIWatcher {

    private final Collection<Watched> components = new ArrayList<>();
    private final Collection<Control> disables = new ArrayList<>();

    public UIWatcher add(TextField textField, Watcher watcher) {
        Watched watched = new TextWatched(textField, watcher);
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

    public UIWatcher addDisableButton(Control control) {
        disables.add(control);
        return this;
    }

    private abstract static class Watched {
        private final Watcher watcher;
        private final Control control;

        Watched(Control control, Watcher watcher) {
            this.control = control;
            this.watcher = watcher;
        }

        public abstract boolean isValid();

        void setInvalid() {
            ObservableList<String> styleClasses = control.getStyleClass();
            if (!styleClasses.contains("invalid")) {
                styleClasses.add("invalid");
            }
            control.applyCss();
        }

        void setValid() {
            ObservableList<String> styleClasses = control.getStyleClass();
            if (styleClasses.contains("invalid")) {
                styleClasses.remove("invalid");
            }
        }

        Watcher getWatcher() {
            return watcher;
        }
    }

    private static class TextWatched extends Watched {

        private final TextField textField;

        TextWatched(TextField textField, Watcher watcher) {
            super(textField, watcher);
            this.textField = textField;
        }

        @Override
        public boolean isValid() {
            return getWatcher().isValid(textField.getText());
        }
    }
}
