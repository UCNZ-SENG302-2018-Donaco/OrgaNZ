package seng302.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.Utilities.Page;

import java.io.IOException;

public class HistoryController {

    public TableColumn timeCol;
    public TableColumn typeCol;
    public TableColumn commandCol;
    @FXML
    Pane sidebarPane;

    @FXML
    private void initialize() {
        SidebarController.loadSidebar(sidebarPane);

    }

    public void loadHistory() {

    }

}
