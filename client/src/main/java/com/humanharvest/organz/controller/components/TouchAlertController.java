package com.humanharvest.organz.controller.components;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.humanharvest.organz.touch.MultitouchHandler;

public class TouchAlertController {

    @FXML
    private Text title;
    @FXML
    private Text body;
    @FXML
    private Button cancelButton;
    @FXML
    private Pane pageHolder;

    private Stage stage;
    private Pane pane;
    private Runnable onOk;

    @FXML
    public void initialize() {
        pageHolder.getStyleClass().add("window");
    }

    public void setup(Alert.AlertType alertType, String title, String body, Stage stage, Pane pane,
            Runnable onOk) {
        this.title.setText(alertType + ": " + title);
        this.body.setText(body);
        this.stage = stage;
        this.pane = pane;
        this.onOk = onOk;

        if (alertType != Alert.AlertType.CONFIRMATION) {
            cancelButton.setVisible(false);
            cancelButton.setManaged(false);
        }
    }

    @FXML
    private void ok() {
        if (onOk != null) {
            onOk.run();
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