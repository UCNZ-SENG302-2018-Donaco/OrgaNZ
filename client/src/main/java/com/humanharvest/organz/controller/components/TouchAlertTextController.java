package com.humanharvest.organz.controller.components;

import com.humanharvest.organz.MultitouchHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TouchAlertTextController {

    private final ObjectProperty<Boolean> resultProperty = new SimpleObjectProperty<>();
    @FXML
    private Text title;
    @FXML
    private Button cancelButton;
    @FXML
    private Pane pageHolder;
    @FXML
    private TextField textInput;
    private Stage stage;
    private Pane pane;

    public TouchAlertTextController(boolean status, String text) {
        textInput = new TextField(text);
        resultProperty.setValue(status);
    }

    public TouchAlertTextController() {

    }

    @FXML
    public void initialize() {
        pageHolder.getStyleClass().add("window");
    }

    public void setup(String title, String body, Stage stage, Pane pane) {
        this.title.setText(title);
        this.stage = stage;
        this.pane = pane;
    }

    public Property<Boolean> getResultProperty() {
        return resultProperty;
    }

    public String getText() {
        return textInput.getText();
    }

    @FXML
    private void ok() {
        resultProperty.setValue(true);
        MultitouchHandler.removePane(pane);
        stage.close();
    }

    @FXML
    private void cancel() {
        resultProperty.setValue(false);
        MultitouchHandler.removePane(pane);
        stage.close();
    }
}
