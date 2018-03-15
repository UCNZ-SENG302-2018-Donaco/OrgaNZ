package seng302.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import seng302.AppUI;
import seng302.Donor;
import seng302.DonorManager;
import seng302.Utilities.Organ;
import seng302.Utilities.OrganAlreadyRegisteredException;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static seng302.Utilities.Organ.*;

public class RegisterOrgansController {
	@FXML
	private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
			checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
	private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();
	@FXML
	private TextField fieldUserID;
	private DonorManager manager;
	private Donor donor;
	private Map<Organ, Boolean> originalStatus;

	@FXML
	private void initialize() {
		organCheckBoxes.put(LIVER, checkBoxLiver);
		organCheckBoxes.put(KIDNEY, checkBoxKidney);
		organCheckBoxes.put(PANCREAS, checkBoxPancreas);
		organCheckBoxes.put(HEART, checkBoxHeart);
		organCheckBoxes.put(LUNG, checkBoxLung);
		organCheckBoxes.put(INTESTINE, checkBoxIntestine);
		organCheckBoxes.put(CORNEA, checkBoxCornea);
		organCheckBoxes.put(MIDDLE_EAR, checkBoxMiddleEar);
		organCheckBoxes.put(SKIN, checkBoxSkin);
		organCheckBoxes.put(BONE, checkBoxBone);
		organCheckBoxes.put(BONE_MARROW, checkBoxBoneMarrow);
		organCheckBoxes.put(CONNECTIVE_TISSUE, checkBoxConnTissue);
		setCheckboxesDisabled();

		// TEST DATA REMOVE THESE //
		manager = AppUI.getManager();
		Donor testDonor1 = new Donor("Alex", "", "Tompkins", LocalDate.of(1998, 9, 5), 5);
		manager.addDonor(testDonor1);
		try {
			manager.getDonorByID(5).setOrganStatus(KIDNEY, true);
		} catch (OrganAlreadyRegisteredException exc) {}

		manager = AppUI.getManager();
		Donor testDonor2 = new Donor("Dummy", "", "Tompkins", LocalDate.of(1990, 1, 1), 6);
		manager.addDonor(testDonor2);
		try {
			manager.getDonorByID(6).setOrganStatus(PANCREAS, true);
			manager.getDonorByID(6).setOrganStatus(INTESTINE, true);
		} catch (OrganAlreadyRegisteredException exc) {}
		// REMOVE THESE //
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
			originalStatus = donor.getOrganStatus();
			for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
				entry.getValue().setSelected(originalStatus.get(entry.getKey()));
			}
		} else {
			setCheckboxesDisabled();
		}
	}

	@FXML
	private void modifyOrgans(ActionEvent event) {
		for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
			try {
				if (originalStatus.get(entry.getKey()) != entry.getValue().isSelected()) {
					donor.setOrganStatus(entry.getKey(), entry.getValue().isSelected());
				}
			} catch (OrganAlreadyRegisteredException exc) {
				System.err.println(exc.getMessage() + " " + entry.getKey());
			}
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

	@FXML
	private void goBack(ActionEvent event) {
		PageNavigator.loadPage(Page.LANDING.getPath());
	}
}
