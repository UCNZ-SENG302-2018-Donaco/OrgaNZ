package seng302.Controller.Client;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyClientOrgansAction;
import seng302.Client;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.Session;
import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the register organs page.
 */
public class RegisterOrganDonationController extends SubController {

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

    public RegisterOrganDonationController() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all checkboxes with their respective Organ to the organCheckBoxes map.
     * - Disables all checkboxes.
     * - Gets the ClientManager and ActionInvoker from the current state.
     * - If a client is logged in, populates with their info and removes ability to view a different client.
     * - If the viewUserId is set, populates with their info.
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
        setCheckboxesDisabled();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            idPane.setDisable(true);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
        }

        mainController.setTitle("Organ donation registration: " + client.getFullName());
        fieldUserID.setText(Integer.toString(client.getUid()));
        updateUserID();
    }

    @Override
    public void refresh() {
        if (client != null) {
            setCheckBoxesEnabled();
            for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
                entry.getValue().setSelected(client.getOrganDonationStatus().get(entry.getKey()));
            }
            HistoryItem save = new HistoryItem("UPDATE ID", "The Client's ID was updated to " + client.getUid());
            JSONConverter.updateHistory(save, "action_history.json");
        } else {
            setCheckboxesDisabled();
        }
    }

    /**
     * Updates the current client to the one specified in the userID field, and populates with their info.
     */
    @FXML
    private void updateUserID() {
        try {
            client = manager.getClientByID(Integer.parseInt(fieldUserID.getText()));
        } catch (NumberFormatException exc) {
            client = null;
        }
        refresh();
    }

    /**
     * Checks which organs check boxes have been changed, and applies those changes with a ModifyClientOrgansAction.
     */
    @FXML
    private void modifyOrgans() {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(client);
        boolean hasChanged = false;

        for (Organ organ : organCheckBoxes.keySet()) {
            boolean oldStatus = client.getOrganDonationStatus().get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                try {
                    action.addChange(organ, newStatus);
                    hasChanged = true;
                } catch (OrganAlreadyRegisteredException e) {
                    e.printStackTrace();
                }
            }
        }
        if (hasChanged) {
            String actionText = invoker.execute(action);
            PageNavigator.refreshAllWindows();
            HistoryItem save = new HistoryItem("UPDATE ORGANS",
                    "The Client's organs were updated: " + client.getOrganStatusString("donations"));
            JSONConverter.updateHistory(save, "action_history.json");

            Notifications.create()
                    .title("Updated Organs")
                    .text(actionText)
                    .showInformation();
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

    @FXML
    private void returnToViewClient() {
        PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
    }
}
