package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import seng302.Actions.ActionInvoker;
import seng302.Actions.ModifyDonorOrgansAction;
import seng302.HistoryItem;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Organ;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the register organs page.
 */
public class RegisterOrgansController {
    @FXML
    private Pane sidebarPane;
    @FXML
    private Pane idPane;
	@FXML
	private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
			checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
	private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();
	@FXML
	private TextField fieldUserID;

	private DonorManager manager;
	private ActionInvoker invoker;
	private Donor donor;

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all checkboxes with their respective Organ to the organCheckBoxes map.
     * - Disables all checkboxes.
     * - Gets the DonorManager and ActionInvoker from the current state.
     * - If a donor is logged in, populates with their info and removes ability to view a different donor.
     * - If the viewUserId is set, populates with their info.
     */
	@FXML
	private void initialize() {
		SidebarController.loadSidebar(sidebarPane);

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

        manager = State.getManager();
        invoker = State.getInvoker();

        String currentUserType = (String) State.getPageParam("currentUserType");
        if (currentUserType == null) {
            Integer viewUserId = (Integer) State.getPageParam("viewUserId");
            if (viewUserId != null) {
                fieldUserID.setText(viewUserId.toString());
                updateUserID(null);
            }
        } else if (currentUserType.equals("donor")) {
            Integer currentUserId = (Integer) State.getPageParam("currentUserId");
            if (currentUserId != null) {
                fieldUserID.setText(currentUserId.toString());
                updateUserID(null);
            }
            idPane.setVisible(false);
            idPane.setManaged(false);
        }
	}

    /**
     * Updates the current donor to the one specified in the userID field, and populates with their info.
     * @param event When ENTER is pressed with focus on the userID field.
     */
	@FXML
	private void updateUserID(ActionEvent event) {
		try {
			donor = manager.getDonorByID(Integer.parseInt(fieldUserID.getText()));
		} catch (NumberFormatException exc) {
			donor = null;
		}

		if (donor != null) {
            State.setPageParam("viewUserId", donor.getUid());
			setCheckBoxesEnabled();
			for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
				entry.getValue().setSelected(donor.getOrganStatus().get(entry.getKey()));
			}
			HistoryItem save = new HistoryItem("UPDATE ID", "The Donor's ID was updated to " + donor.getUid());
			JSONConverter.updateHistory(save, "action_history.json");
		} else {
			setCheckboxesDisabled();
		}
	}

    /**
     * Checks which organs check boxes have been changed, and applies those changes with a ModifyDonorOrgansAction.
     * @param event When any organ checkbox changes state.
     */
	@FXML
	private void modifyOrgans(ActionEvent event) {
	    ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(donor);
	    boolean hasChanged = false;

		for (Organ organ : organCheckBoxes.keySet()) {
		    boolean oldStatus = donor.getOrganStatus().get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                action.addChange(organ, newStatus);
                hasChanged = true;
            }
		}
		if (hasChanged) {
            invoker.execute(action);
			HistoryItem save = new HistoryItem("UPDATE ORGANS", "The Donor's organs were updated: " + donor.getDonorOrganStatusString());
			JSONConverter.updateHistory(save, "action_history.json");
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
