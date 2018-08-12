package com.humanharvest.organz.controller.components;

import com.humanharvest.organz.AppTUIO;
import com.humanharvest.organz.utilities.view.BooleanCallback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TouchAlertController {

    @FXML
    private Text title;

    @FXML
    private Text body;

    private BooleanCallback callback;
    private Stage stage;
    private Pane pane;

    private ObjectProperty<Boolean> resultProperty = new SimpleObjectProperty<>();

    public TouchAlertController() {
        System.out.println("Made controller");
    }

    public TouchAlertController(String title, String body) {
        this.title.setText(title);
        this.body.setText(body);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setBody(String body) {
        this.body.setText(body);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Property<Boolean> getResultProperty() {
        return resultProperty;
    }

    @FXML
    private void ok() {
        resultProperty.setValue(true);
        AppTUIO.root.getChildren().remove(pane);
        stage.close();
    }

    @FXML
    private void cancel() {
        resultProperty.setValue(false);
        AppTUIO.root.getChildren().remove(pane);
        stage.close();
    }
}
