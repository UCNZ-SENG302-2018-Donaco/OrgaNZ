package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.security.cert.CertPathValidatorException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ManuallyOverwrittenOrgansController extends SubController {

    private static final int ROWS_PER_PAGE = 30;
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy hh:mm a");




    @FXML
    public HBox menuBarPane;

    @FXML
    public Pagination pagination;

    @FXML
    public Text displayingXToYOfZText;

    @FXML
    public TableView<DonatedOrgan> tableView;

    @FXML
    public TableColumn<DonatedOrgan, Organ> organCol;

    @FXML
    public TableColumn<DonatedOrgan, String> reasonCol;

    @FXML
    public TableColumn<DonatedOrgan, String> clinicianCol;

    @FXML
    public TableColumn<DonatedOrgan, LocalDateTime> manualOverrideCol;

    private ClientManager manager;
    private ObservableList<DonatedOrgan> manuallyExpiredOrgans = FXCollections.observableArrayList();
    private DonatedOrgan selectedOrgan;

    public ManuallyOverwrittenOrgansController() { manager = State.getClientManager(); }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.setTitle("Organs to donate");
        mainController.loadMenuBar(menuBarPane);
        refresh();
    }

    @FXML
    private void initialize() {

    }

    private void setupTable() {

    }
}
