package com.humanharvest.organz.controller.client;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.ModifyClientOrgansAction;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.utilities.JSONConverter;
import com.humanharvest.organz.utilities.resolvers.ClientResolver;
import com.humanharvest.organz.utilities.resolvers.client.ModifyClientOrganDonationResolver;
import com.humanharvest.organz.utilities.view.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the register organs page.
 */
public class RegisterOrganDonationController extends SubController {

    private Session session;
    private ClientManager manager;
    private ActionInvoker invoker;
    private Client client;
    private Map<Organ, Boolean> donationStatus;

    @FXML
    private Pane sidebarPane, idPane, menuBarPane;
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


        if (session.getLoggedInUserType() == Session.UserType.CLIENT) {
            client = session.getLoggedInClient();
            idPane.setDisable(true);
            mainController.loadSidebar(sidebarPane);
        } else if (windowContext.isClinViewClientWindow()) {
            client = windowContext.getViewClient();
            mainController.loadMenuBar(menuBarPane);
        }
        fieldUserID.setText(Integer.toString(client.getUid()));
        updateUserID();


    }

    @Override
    public void refresh() {
        if (client != null) {
            setCheckBoxesEnabled();

            List<TransplantRequest> transplantRequests = ClientResolver.getTransplantRequests(client.getUid());
            donationStatus = ClientResolver.getOrganDonationStatus(client.getUid());

            EnumSet<Organ> allPreviouslyRequestedOrgans = transplantRequests
                    .stream()
                    .map(TransplantRequest::getRequestedOrgan)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Organ.class)));

            for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
                entry.getValue().setSelected(donationStatus.get(entry.getKey()));
                if (allPreviouslyRequestedOrgans.contains(entry.getKey())) {
                    entry.getValue().setStyle("-fx-color: lightcoral;");
                    entry.getValue().setTooltip(new Tooltip("This organ was/is part of a transplant request."));
                } else {
                    entry.getValue().setStyle(null);
                    entry.getValue().setTooltip(null);
                }
            }
            if (session.getLoggedInUserType() == UserType.CLIENT) {
                mainController.setTitle("Register Organs:  " + client.getPreferredName());
            } else if (windowContext.isClinViewClientWindow()) {
                mainController.setTitle("Register Organs:  " + client.getFullName());
            }
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
    private void apply() {
        ModifyClientOrganDonationResolver resolver = new ModifyClientOrganDonationResolver(client);
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(client, State.getClientManager());
        boolean hasChanged = false;

        for (Organ organ : organCheckBoxes.keySet()) {
            boolean oldStatus = donationStatus.get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                resolver.addChange(organ, newStatus);
                hasChanged = true;
            }
        }
        if (hasChanged) {
            resolver.execute();
            String actionText = "test";
            HistoryItem save = new HistoryItem("UPDATE ORGANS",
                    "The Client's organs were updated: " + client.getOrganStatusString("donations"));
            JSONConverter.updateHistory(save, "action_history.json");

            PageNavigator.refreshAllWindows();
            Notifications.create()
                    .title("Updated Donating Organs")
                    .text(actionText)
                    .showInformation();
        } else {
            Notifications.create()
                    .title("No changes were made.")
                    .text("No changes were made to the client's organ status.")
                    .showWarning();
        }
    }

    /**
     * Resets the page back to its default state.
     */
    @FXML
    private void cancel() {
        refresh();
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
