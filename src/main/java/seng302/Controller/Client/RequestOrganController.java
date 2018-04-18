package seng302.Controller.Client;

import java.util.HashMap;
import java.util.Map;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyOrganRequestAction;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

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

    public RequestOrganController() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }


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
        setCheckboxesDisabled();
    }


    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        fieldUserID.setText(Integer.toString(client.getUid()));
        updateUserID(null);
    }

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
                    "The Client's organ request list was updated: " + client.getClientOrganRequestStatusString());
            JSONConverter.updateHistory(organRequest, "action_history.json");
        }
    }

    @FXML
    private void viewRequestHistory(ActionEvent event) {
        for (int i = 0; i < client.getTransplantRequests().size(); i++) {
            System.out.println( "Organ: " + client.getTransplantRequests().get(i).getRequestedOrgan() + "| Date: " +
                    client.getTransplantRequests().get(i).getRequestTime());
        }
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
            setCheckBoxesEnabled();
            for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
                entry.getValue().setSelected(client.getOrganRequestStatus().get(entry.getKey()));
            }
            HistoryItem save = new HistoryItem("UPDATE ID", "The Clients ID was updated to " + client.getUid());
            JSONConverter.updateHistory(save, "action_history.json");
        } else {
            setCheckboxesDisabled();
        }
    }


    /**
     * Sets the state of all checkboxes to not selected, then disables them.
     */
    private void setCheckboxesDisabled() {
        for (CheckBox box : organCheckBoxes.values()) {
            box.setSelected(false);
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

}
