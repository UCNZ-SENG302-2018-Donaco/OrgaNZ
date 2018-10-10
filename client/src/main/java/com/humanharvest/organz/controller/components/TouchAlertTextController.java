package com.humanharvest.organz.controller.components;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.humanharvest.organz.touch.MultitouchHandler;

public class TouchAlertTextController {

    @FXML
    private Text title, heading, instructions;
    @FXML
    private Pane pageHolder;
    @FXML
    private TextField textInput;

    private Stage stage;
    private Pane pane;
    private Consumer<String> onSubmit;

    public TouchAlertTextController() {

    }

    @FXML
    public void initialize() {
        pageHolder.getStyleClass().add("window");
    }

    public void setup(String title, String body, String instructions, String prefilledText,
            Stage stage, Pane pane, Consumer<String> onSubmit) {
        this.title.setText(title);
        this.heading.setText(body);
        this.instructions.setText(instructions);
        this.textInput.setText(prefilledText);
        this.stage = stage;
        this.pane = pane;
        this.onSubmit = onSubmit;
    }

    @FXML
    private void ok() {
        if (onSubmit != null) {
            onSubmit.accept(textInput.getText());
        }
        MultitouchHandler.removePane(pane);
        stage.close();
    }

    @FXML
    private void cancel() {
        MultitouchHandler.removePane(pane);
        stage.close();
    }
}
