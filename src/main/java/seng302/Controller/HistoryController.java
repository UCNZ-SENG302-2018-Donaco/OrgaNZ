package seng302.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.Utilities.Page;

import java.io.IOException;

public class HistoryController {

    @FXML
    Pane sidebarPane;

    @FXML
    private void initialize() {
        // IMPORTING SIDEBAR //
        try {
            VBox sidebar = FXMLLoader.load(getClass().getResource(Page.SIDEBAR.getPath()));
            sidebarPane.getChildren().setAll(sidebar);
        } catch (IOException exc) {
            System.err.println("Couldn't load sidebar from fxml file.");
            exc.printStackTrace();
        }

    }

}
