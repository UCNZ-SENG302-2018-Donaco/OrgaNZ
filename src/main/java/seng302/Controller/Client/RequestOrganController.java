package seng302.Controller.Client;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyOrganRequestAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

/**
 * Presents an interface displaying all organs currently requested by a user. Clinicians have the ability to alter what
 * organs are currently being requested by a Client.
 */
public class RequestOrganController extends SubController {

    private Session session;
    private ClientManager manager;
    private ActionInvoker invoker;
    private Client client;

    @FXML
    private Pane sidebarPane, idPane;
    @FXML
    private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
            checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
    private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();
    @FXML
    private TextField fieldUserID;
    @FXML
    private Button requestHistoryButton;

    public RequestOrganController() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Map each organ to the matching checkbox.
     */
    @FXML
    private void initialize() {
        organCheckBoxes.put(Organ.LIVER, checkBoxLiver);
        organCheckBoxes.put(Organ.KIDNEY, checkBoxKidney);
        organCheckBoxes.put(Organ.PANCREAS, checkBoxPancreas);
        organCheckBoxes.put(Organ.HEART, checkBoxHeart);
        organCheckBoxes.put(Organ.LUNG, checkBoxLung);
        organCheckBoxes.put(Organ.INTESTINE, checkBoxIntestine);
        organCheckBoxes.put(Organ.CORNEA, checkBoxCornea);
        organCheckBoxes.put(Organ.MIDDLE_EAR, checkBoxMiddleEar);
        organCheckBoxes.put(Organ.SKIN, checkBoxSkin);
        organCheckBoxes.put(Organ.BONE, checkBoxBone);
        organCheckBoxes.put(Organ.BONE_MARROW, checkBoxBoneMarrow);
        organCheckBoxes.put(Organ.CONNECTIVE_TISSUE, checkBoxConnTissue);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            fieldUserID.setEditable(false);
            setCheckboxesDisabled();
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            fieldUserID.setEditable(true);
            setCheckBoxesEnabled();
        }

        mainController.setTitle("Organ request registration: " + client.getFullName());
        fieldUserID.setText(Integer.toString(client.getUid()));
        updateUserID(null);
    }

    /**
     * When an organ checkbox is ticked or unticked, this creates a ModifyOrganRequestAction to record the change and
     * update the Clients transplantRequest list.
     * @param event a checkbox is checked/unchecked.
     */
    @FXML
    private void modifyRequests(ActionEvent event) {
        ModifyOrganRequestAction action = new ModifyOrganRequestAction(client);
        boolean hasChanged = false;

        for (Organ organ: organCheckBoxes.keySet()) {
            boolean oldStatus = client.getOrganRequestStatus().get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                action.addChange(organ, newStatus);
                hasChanged = true;
            }
        }
        if (hasChanged) {
            invoker.execute(action);
            HistoryItem organRequest = new HistoryItem("ORGAN REQUEST UPDATE",
                    "The Client's organ request list was updated: " + client.getOrganStatusString("requests"));
            JSONConverter.updateHistory(organRequest, "action_history.json");
        }
    }

    /**
     * Navigates to the organ_request_history page.
     * @param event the back view history button is clicked.
     */
    @FXML
    private void viewRequestHistory(ActionEvent event) {
        PageNavigator.loadPage(Page.ORGAN_REQUEST_HISTORY, mainController);
    }

    /**
     * Updates the current client to the one specified in the userID field, and populates with their info.
     * @param event When ENTER is pressed with focus on the userID field.
     */
    @FXML
    private void updateUserID(ActionEvent event) {
        try {
            client = manager.getClientByID(Integer.parseInt(fieldUserID.getText()));
        } catch (NumberFormatException exc) {
            client = null;
        }

        if (client != null) {
            requestHistoryButton.setDisable(false);
            for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
                entry.getValue().setSelected(client.getOrganRequestStatus().get(entry.getKey()));
            }
            HistoryItem save = new HistoryItem("UPDATE ID", "The Clients ID was updated to " + client.getUid());
            JSONConverter.updateHistory(save, "action_history.json");
        } else {
            setCheckboxesDisabled();
            setCheckBoxesUnselected();
            requestHistoryButton.setDisable(true);
        }
    }

    /**
     * Disables all checkboxes.
     */
    private void setCheckboxesDisabled() {
        for (CheckBox box : organCheckBoxes.values()) {
            box.setDisable(true);
        }
    }

    /**
     * Enables all checkboxes.
     */
    private void setCheckBoxesEnabled() {
        for (CheckBox box : organCheckBoxes.values()) {
            box.setDisable(false);
        }
    }

    /**
     * Unselect all checkboxes.
     */
    private void setCheckBoxesUnselected() {
        for (CheckBox box: organCheckBoxes.values()) {
            box.setSelected(false);
        }
    }
}
