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

public class RegisterOrgansController {
	@FXML
	private CheckBox checkBoxLiver, checkBoxKidney, checkBoxPancreas, checkBoxHeart, checkBoxLung, checkBoxIntestine,
			checkBoxCornea, checkBoxMiddleEar, checkBoxSkin, checkBoxBone, checkBoxBoneMarrow, checkBoxConnTissue;
	private final Map<Organ, CheckBox> organCheckBoxes = new HashMap<>();
	@FXML
	private TextField fieldUserID;
	private DonorManager manager;
	private Donor donor;

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

		// TODO TEST DATA REMOVE THESE //
		manager = AppUI.getManager();
		Donor testDonor1 = new Donor("Alex", "", "Tompkins", LocalDate.of(1998, 9, 5), 5);
		manager.addDonor(testDonor1);
		try {
			manager.getDonorByID(5).setOrganStatus(Organ.KIDNEY, true);
		} catch (OrganAlreadyRegisteredException exc) {}

		manager = AppUI.getManager();
		Donor testDonor2 = new Donor("Dummy", "", "Tompkins", LocalDate.of(1990, 1, 1), 6);
		manager.addDonor(testDonor2);
		try {
			manager.getDonorByID(6).setOrganStatus(Organ.PANCREAS, true);
			manager.getDonorByID(6).setOrganStatus(Organ.INTESTINE, true);
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
			for (Map.Entry<Organ, CheckBox> entry : organCheckBoxes.entrySet()) {
				entry.getValue().setSelected(donor.getOrganStatus().get(entry.getKey()));
			}
		} else {
			setCheckboxesDisabled();
		}
	}

	@FXML
	private void modifyOrgans(ActionEvent event) {
		for (Organ organ : organCheckBoxes.keySet()) {
			try {
				if (donor.getOrganStatus().get(organ) != organCheckBoxes.get(organ).isSelected()) {
					donor.setOrganStatus(organ, organCheckBoxes.get(organ).isSelected());
				}
			} catch (OrganAlreadyRegisteredException exc) {
				System.err.println(exc.getMessage() + " " + organ);
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
