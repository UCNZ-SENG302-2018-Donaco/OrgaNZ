package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import seng302.Actions.ActionInvoker;
import seng302.Actions.ModifyDonorAction;
import seng302.Donor;
import seng302.DonorManager;
import seng302.HistoryItem;
import seng302.Session;
import seng302.State;
import seng302.Utilities.BloodType;
import seng302.Utilities.Gender;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.Page;
import seng302.Utilities.PageNavigator;
import seng302.Utilities.Region;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the view/edit donor page.
 */
public class ViewDonorController extends SubController {
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

    private Session session;
    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor viewedDonor;

    @FXML
    private Pane sidebarPane, idPane, inputsPane;
    @FXML
	private Label creationDate, lastModified, noDonorLabel, fnameLabel, lnameLabel, dobLabel,
            dodLabel, heightLabel, weightLabel, ageDisplayLabel, ageLabel, BMILabel;
	@FXML
	private TextField id, fname, lname, mname, height, weight, address;
	@FXML
	private DatePicker dob, dod;
	@FXML
    private ChoiceBox<Gender> gender;
	@FXML
    private ChoiceBox<BloodType> btype;
	@FXML
    private ChoiceBox<Region> region;

	public ViewDonorController() {
        manager = State.getDonorManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    /**
     * Initializes the UI for this page.
     * - Loads the sidebar.
     * - Adds all values to the gender and blood type dropdown lists.
     * - Disables all fields.
     * - If a donor is logged in, populates with their info and removes ability to view a different donor.
     * - If the viewUserId is set, populates with their info.
     */
	@FXML
    private void initialize() {
	    gender.setItems(FXCollections.observableArrayList(Gender.values()));
        btype.setItems(FXCollections.observableArrayList(BloodType.values()));
        region.setItems(FXCollections.observableArrayList(Region.values()));
		setFieldsDisabled(true);
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            viewedDonor = session.getLoggedInDonor();
            idPane.setVisible(false);
            idPane.setManaged(false);
        } else if (windowContext.isClinViewDonorWindow()) {
            viewedDonor = windowContext.getViewDonor();
        }

        if (viewedDonor != null) {
            id.setText(Integer.toString(viewedDonor.getUid()));
            searchDonor();
        }
    }

	/**
	 * Searches for a donor based off the id number supplied in the text field. The users fields will be displayed if
	 * this user exists, otherwise an error message will display.
	 */
	@FXML
	private void searchDonor() {
		int id_value;
		try {
			id_value = Integer.parseInt(id.getText());
		} catch (Exception e) {
			noDonorLabel.setVisible(true);
			setFieldsDisabled(true);
			return;
		}

		viewedDonor = manager.getDonorByID(id_value);
		if (viewedDonor == null) {
			noDonorLabel.setVisible(true);
            setFieldsDisabled(true);
		} else {
			noDonorLabel.setVisible(false);
            setFieldsDisabled(false);

			fname.setText(viewedDonor.getFirstName());
			lname.setText(viewedDonor.getLastName());
			mname.setText(viewedDonor.getMiddleName());
			dob.setValue(viewedDonor.getDateOfBirth());
			dod.setValue(viewedDonor.getDateOfDeath());
			gender.setValue(viewedDonor.getGender());
			height.setText(String.valueOf(viewedDonor.getHeight()));
			weight.setText(String.valueOf(viewedDonor.getWeight()));
			btype.setValue(viewedDonor.getBloodType());
			region.setValue(viewedDonor.getRegion());
			address.setText(viewedDonor.getCurrentAddress());

			creationDate.setText(viewedDonor.getCreationdate().format(dateTimeFormat));
			if (viewedDonor.getModified_on() == null) {
			    lastModified.setText("User has not been modified yet.");
            } else {
                lastModified.setText(viewedDonor.getModified_on().format(dateTimeFormat));
            }

			HistoryItem save = new HistoryItem("SEARCH DONOR",
					"Donor " + viewedDonor.getFirstName() + " " + viewedDonor.getLastName() + " (" + viewedDonor.getUid() + ") was searched");
			JSONConverter.updateHistory(save, "action_history.json");

			displayBMI();
			displayAge();
		}
	}

	/**
	 * Disables the view of user fields as these will all be irrelevant to the id number supplied if no such donor
	 * exists with this id. Or sets it to visible so that the user can see all fields relevant to the donor.
	 * @param disabled the state of the pane.
	 */
    private void setFieldsDisabled(boolean disabled) {
        inputsPane.setVisible(!disabled);
    }

	/**
	 * Saves the changes a user makes to the viewed donor if all their inputs are valid. Otherwise the invalid fields
	 * text turns red.
	 */
	@FXML
	private void saveChanges() {
		if (checkMandatoryFields() && checkNonMandatoryFields()) {
			updateChanges();
			displayBMI();
			displayAge();
			lastModified.setText(viewedDonor.getModified_on().format(dateTimeFormat));
            //TODO show what in particular was updated
            HistoryItem save = new HistoryItem("UPDATE DONOR INFO",
                    "Updated changes to donor " + viewedDonor.getFirstName() + " " + viewedDonor.getLastName() + "updated donor info: " + viewedDonor.getDonorInfoString());
            JSONConverter.updateHistory(save, "action_history.json");
		}
	}

	/**
	 * Checks that all mandatory fields have valid arguments inside. Otherwise display red text on the invalidly entered
	 * labels.
	 * @return true if all mandatory fields have valid input.
	 */
	private boolean checkMandatoryFields() {
		boolean update = true;
		if (fname.getText().equals("")) {
			fnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			fnameLabel.setTextFill(Color.BLACK);
		}

		if (lname.getText().equals("")) {
			lnameLabel.setTextFill(Color.RED);
			update = false;
		} else {
			lnameLabel.setTextFill(Color.BLACK);
		}
		if (dob.getValue() == null || dob.getValue().isAfter(LocalDate.now())) {
			dobLabel.setTextFill(Color.RED);
			update = false;
		} else {
			dobLabel.setTextFill(Color.BLACK);
		}
		return update;
	}


	/**
	 * Checks that non mandatory fields have either valid input, or no input. Otherwise red text is shown.
	 * @return true if all non mandatory fields have valid/no input.
	 */
	private boolean checkNonMandatoryFields() {
		boolean update = true;
		if (dod.getValue() == null || dod.getValue().isBefore(LocalDate.now())) {
			dodLabel.setTextFill(Color.BLACK);
		} else {
			dodLabel.setTextFill(Color.RED);
			update = false;
		}

		try {
			double h = Double.parseDouble(height.getText());
			if( h < 0) {
				heightLabel.setTextFill(Color.RED);
				update = false;
			} else {
				heightLabel.setTextFill(Color.BLACK);
			}

		} catch (NumberFormatException ex) {
			heightLabel.setTextFill(Color.RED);
		}

		try {
			double w = Double.parseDouble(weight.getText());
			if( w < 0) {
				weightLabel.setTextFill(Color.RED);
				update = false;
			} else {
				weightLabel.setTextFill(Color.BLACK);
			}

		} catch (NumberFormatException ex) {
			weightLabel.setTextFill(Color.RED);
			update = false;
		}
		return update;
	}

	/**
	 * Records the changes updated as a ModifyDonorAction to trace the change in record.
	 */
	private void updateChanges() {
		try {
			ModifyDonorAction action = new ModifyDonorAction(viewedDonor);

			action.addChange("setFirstName", viewedDonor.getFirstName(), fname.getText());
			action.addChange("setLastName", viewedDonor.getLastName(), lname.getText());
			action.addChange("setMiddleName", viewedDonor.getMiddleName(), mname.getText());
			action.addChange("setDateOfBirth", viewedDonor.getDateOfBirth(), dob.getValue());
			action.addChange("setDateOfDeath", viewedDonor.getDateOfDeath(), dod.getValue());
			action.addChange("setGender", viewedDonor.getGender(), gender.getValue());
			action.addChange("setHeight", viewedDonor.getHeight(), Double.parseDouble(height.getText()));
			action.addChange("setWeight", viewedDonor.getWeight(), Double.parseDouble(weight.getText()));
			action.addChange("setBloodType", viewedDonor.getBloodType(), btype.getValue());
            action.addChange("setRegion", viewedDonor.getRegion(), region.getValue());
			action.addChange("setCurrentAddress", viewedDonor.getCurrentAddress(), address.getText());

		    invoker.execute(action);

		    PageNavigator.showAlert(Alert.AlertType.INFORMATION,
				"Success",
				String.format("Successfully updated donor %s %s %s %d.",
						viewedDonor.getFirstName(), viewedDonor.getMiddleName(),
						viewedDonor.getLastName(), viewedDonor.getUid()));

		} catch (NoSuchFieldException | NoSuchMethodException exc) {
			exc.printStackTrace();
		}
	}

	/**
	 * Displays the currently viewed donors BMI.
	 */
	private void displayBMI() {
		if (viewedDonor.getDateOfDeath() == null) {
			BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
		} else {
			BMILabel.setText(String.format("%.01f", viewedDonor.getBMI()));
		}
	}

	/**
	 * Displays either the current age, or age at death of the donor depending on if the date of death field has been
	 * filled in.
	 */
	private void displayAge() {
		if (viewedDonor.getDateOfDeath() == null) {
			ageDisplayLabel.setText("Age");
		} else {
			ageDisplayLabel.setText("Age at Death");
		}
		ageLabel.setText(String.valueOf(viewedDonor.getAge()));
	}

	/**
	 * Navigate to the page to display organs for the currently specified donor.
	 */
	@FXML
	public void viewOrgansForDonor() {
		PageNavigator.loadPage(Page.REGISTER_ORGANS, mainController);
	}
}
