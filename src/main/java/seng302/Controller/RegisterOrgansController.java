package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.Actions.ActionInvoker;
import seng302.Actions.ModifyDonorOrgansAction;
import seng302.State;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Organ;
import seng302.Utilities.Page;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterOrgansController {
    @FXML
    Pane sidebarPane;

	@FXML
	private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
			checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
	private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();
	@FXML
	private TextField fieldUserID;
	private DonorManager manager;
	private ActionInvoker invoker;
	private Donor donor;

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
	}

	@FXML
	private void updateUserID(ActionEvent event) {
		try {
			donor = manager.getDonorByID(Integer.parseInt(fieldUserID.getText()));
		} catch (NumberFormatException exc) {
			donor = null;
		}

		if (donor != null) {
			setCheckBoxesEnabled();
			for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
				entry.getValue().setSelected(donor.getOrganStatus().get(entry.getKey()));
			}
		} else {
			setCheckboxesDisabled();
		}
	}

	@FXML
	private void modifyOrgans(ActionEvent event) {
	    ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(donor);
	    boolean hasChanged = false;

		for (Organ organ : organCheckBoxes.keySet()) {
		    boolean oldStatus = donor.getOrganStatus().get(organ);
            boolean newStatus = organCheckBoxes.get(organ).isSelected();

            if (oldStatus != newStatus) {
                action.addChange(organ, oldStatus, newStatus);
                hasChanged = true;
            }
		}
		if (hasChanged) {
            invoker.execute(action);
        }
	}

	private void setCheckboxesDisabled() {
		for (CheckBox box : organCheckBoxes.values()) {
			box.setSelected(false);
			box.setDisable(true);
		}
	}

	private void setCheckBoxesEnabled() {
		for (CheckBox box : organCheckBoxes.values()) {
			box.setDisable(false);
		}
	}
}
