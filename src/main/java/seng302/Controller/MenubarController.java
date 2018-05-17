package seng302.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

import seng302.Actions.ActionInvoker;
import seng302.AppUI;
import seng302.Client;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class MenubarController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(MenubarController.class.getName());

    private static final String ERROR_SAVING_MESSAGE = "There was an error saving to the file specified.";
    private static final String ERROR_LOADING_MESSAGE = "There was an error loading the file specified.";
    public MenuItem viewClientItem;
    public MenuItem searchClientItem;
    public MenuItem donateOrganItem;
    public MenuItem requestOrganItem;
    public MenuItem viewMedicationsItem;
    public MenuItem medicalHistoryItem;
    public MenuItem proceduresItem;
    public MenuItem searchStaffItem;
    public MenuItem createAdministratorItem;
    public MenuItem createClinicianItem;
    public MenuItem searchTransplantsItem;
    public MenuItem viewAdministratorItem;
    public MenuItem viewClinicianItem;
    public MenuItem historyItem;
    public MenuItem cliItem;
    public MenuItem logOutItem;
    public MenuItem saveItem;
    public MenuItem loadItem;


    public Menu clientPrimaryItem;
    public Menu organPrimaryItem;
    public Menu medicationsPrimaryItem;
    public Menu staffPrimaryItem;
    public Menu transplantsPrimaryItem;
    public Menu profilePrimaryItem;
    public Menu filePrimaryItem;



    private ActionInvoker invoker;
    private Session session;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public MenubarController() {
        invoker = State.getInvoker();
        session = State.getSession();
    }

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        UserType userType = session.getLoggedInUserType();

        Menu jumboMenu[] = {clientPrimaryItem, organPrimaryItem, medicationsPrimaryItem, staffPrimaryItem,
                transplantsPrimaryItem, filePrimaryItem, profilePrimaryItem};
        Menu viewClientMenu[] = {clientPrimaryItem, organPrimaryItem, medicationsPrimaryItem};
        Menu viewClinicianMenu[] = {clientPrimaryItem, transplantsPrimaryItem, profilePrimaryItem};
        Menu viewAdminMenu[] = {clientPrimaryItem, staffPrimaryItem, transplantsPrimaryItem, filePrimaryItem,
                profilePrimaryItem};

        if (userType == UserType.CLIENT || windowContext.isClinViewClientWindow()) {
            hideMenus(jumboMenu);
        } else if (userType == UserType.CLINICIAN) {
            hideMenus(viewAdminMenu);
            hideMenus(viewClientMenu);

            //TODO add administrator rights
        }
        //else if (userType == UserType.ADMINISTRATOR) {
           // hideMenus(viewClinicianMenu);
          //  hideMenus(viewClientMenu);
       // }

        if (windowContext.isClinViewClientWindow()) {
            hideMenus(viewClinicianMenu);
            hideMenus(viewAdminMenu);
        }


        //undoButton.setDisable(!invoker.canUndo());
        //redoButton.setDisable(!invoker.canRedo());
    }

    /**
     * Evaluates if the request organs button should be displayed for the current user.
     * @param userType the type of current user
     * @return true if the button should be shown, false otherwise
     */
    private boolean shouldShowRequestOrgans(UserType userType) {
        if (userType == UserType.CLIENT) {
            Client currentClient = session.getLoggedInClient();
            return currentClient.isReceiver();
        } else {
            return windowContext.isClinViewClientWindow();
        }
    }

    /**
     * Hides all the buttons in the passed-in array.
     * @param items The buttons to hide
     */
    private void hideMenuItems(MenuItem items[]) {
        for (MenuItem menuItem : items) {
            hideMenuItem(menuItem);
        }
    }

    /**
     * Hides the button from the sidebar.
     * @param menuItem the button to hide
     */
    private void hideMenuItem(MenuItem menuItem) {
        menuItem.setVisible(false);
        //menuItem.setManaged(false);
    }

    /**
     * Hides all the buttons in the passed-in array.
     * @param menus The buttons to hide
     */
    private void hideMenus(Menu menus[]) {
        for (Menu menu : menus) {
            hideMenu(menu);
        }
    }

    /**
     * Hides the Primary menu type from the menu bar.
     * @param menu the menu to hide
     */
    private void hideMenu(Menu menu) {
        menu.setVisible(false);
        //menu.setManaged(false);
    }

    /**
     * Refreshes the undo/redo buttons based on if there are changes to be made
     */
    public void refresh() {
        //TODO get the undo/redo buttons back in the scene.
        //undoButton.setDisable(!invoker.canUndo());
        //redoButton.setDisable(!invoker.canRedo());
    }

    /**
     * Redirects the GUI to the View Client page.
     */
    @FXML
    private void goToViewClient() {
        PageNavigator.loadPage(Page.VIEW_CLIENT, mainController);
    }

    /**
     * Redirects the GUI to the Register Organs page.
     */
    @FXML
    private void goToRegisterOrganDonation() {
        PageNavigator.loadPage(Page.REGISTER_ORGAN_DONATIONS, mainController);
    }

    /**
     * Redirects the GUI to the Request Organs page.
     */
    @FXML
    private void goToRequestOrganDonation() {
        PageNavigator.loadPage(Page.REQUEST_ORGANS, mainController);
    }

    /**
     * Redirects the GUI to the View Medications page.
     */
    @FXML
    private void goToViewMedications() {
        PageNavigator.loadPage(Page.VIEW_MEDICATIONS, mainController);
    }

    /**
     * Redirects the GUI to the View Client page.
     */
    @FXML
    private void goToViewClinician() {
        PageNavigator.loadPage(Page.VIEW_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to the Search clients page.
     */
    @FXML
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

    /**
     * Redirects the GUI to the Transplants page.
     */
    @FXML
    private void goToTransplants() {
        PageNavigator.loadPage(Page.TRANSPLANTS, mainController);
    }

    /**
     * Redirects the GUI to the History page.
     */
    @FXML
    private void goToHistory() {
        PageNavigator.loadPage(Page.HISTORY, mainController);
    }

    /**
     * Redirects the GUI to the Illness History page.
     */
    @FXML
    private void goToIllnessHistory() {
        PageNavigator.loadPage(Page.VIEW_MEDICAL_HISTORY, mainController);
    }

    /**
     * Redirects the GUI to the View Procedures page.
     */
    @FXML
    private void goToViewProcedures() {
        PageNavigator.loadPage(Page.VIEW_PROCEDURES, mainController);
    }

    //TODO administrator rights
/*
    /**
     * Redirects the GUI to the Create administrator page.
     */

/*
    @FXML
    private void goToCreateAdmin() {
        PageNavigator.loadPage(Page.CREATE_ADMINISTRATOR, mainController);
    }
*/
    /**
     * Redirects the GUI to the Create clinician page.
     */
    @FXML
    private void goToCreateClinician() {
        PageNavigator.loadPage(Page.CREATE_CLINICIAN, mainController);
    }

    /**
     * Opens a save file dialog to choose where to save all clients in the system to a file.
     */
    @FXML
    private void save() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Clients File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(AppUI.getWindow());
            if (file != null) {
                JSONConverter.saveToFile(file);

                Notifications.create().title("Saved").text(String.format("Successfully saved %s clients to file %s",
                        State.getClientManager().getClients().size(), file.getName())).showInformation();

                HistoryItem save = new HistoryItem("SAVE", "The systems current state was saved.");
                JSONConverter.updateHistory(save, "action_history.json");

                invoker.resetUnsavedUpdates();
                PageNavigator.refreshAllWindows();
            }
        } catch (URISyntaxException | IOException e) {
            PageNavigator.showAlert(AlertType.WARNING, "Save Failed", ERROR_SAVING_MESSAGE);
            LOGGER.log(Level.SEVERE, ERROR_SAVING_MESSAGE, e);
        }
    }

    /**
     * Opens a load file dialog to choose a file to load all clients from.
     */
    @FXML
    private void load() {

        // Confirm that the user wants to overwrite current data with data from a file
        Optional<ButtonType> response = PageNavigator.showAlert(AlertType.CONFIRMATION,
                "Confirm load from file",
                "Loading from a file will overwrite all current data. Would you like to proceed?");

        if (response.isPresent() && response.get() == ButtonType.OK) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Load Clients File");
                fileChooser.setInitialDirectory(
                        new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                                .getParent().toString())
                );
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
                File file = fileChooser.showOpenDialog(AppUI.getWindow());

                if (file != null) {
                    JSONConverter.loadFromFile(file);

                    HistoryItem load = new HistoryItem("LOAD", "The systems state was loaded from " + file.getName());
                    JSONConverter.updateHistory(load, "action_history.json");

                    mainController.resetWindowContext();
                    Notifications.create().title("Loaded data").text(
                            String.format("Successfully loaded %d clients from file",
                                    State.getClientManager().getClients().size()))
                            .showInformation();
                    PageNavigator.loadPage(Page.LANDING, mainController);
                }
            } catch (URISyntaxException | IOException | IllegalArgumentException e) {
                PageNavigator.showAlert(AlertType.WARNING, "Load Failed",
                        "Warning: unrecognisable or invalid file. please make\n"
                                + "sure that you have selected the correct file type.");
                LOGGER.log(Level.SEVERE, ERROR_LOADING_MESSAGE, e);
            }
        }
    }

    /**
     * Logs out the current user and sends them to the Landing page.
     */
    @FXML
    private void logout() {
        State.logout();
        for (MainController controller : State.getMainControllers()) {
            if (controller != mainController) {
                controller.closeWindow();
            }
        }
        State.getMainControllers().clear();
        State.addMainController(mainController);
        mainController.resetWindowContext();
        PageNavigator.loadPage(Page.LANDING, mainController);
        HistoryItem save = new HistoryItem("LOGOUT", "The user logged out");
        JSONConverter.updateHistory(save, "action_history.json");
    }

    /**
     * Undoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void undo() {
        String undoneText = invoker.undo();
        Notifications.create().title("Undo").text(undoneText).showInformation();
        HistoryItem save = new HistoryItem("UNDO", undoneText);
        JSONConverter.updateHistory(save, "action_history.json");
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void redo() {
        String redoneText = invoker.redo();
        Notifications.create().title("Redo").text(redoneText).showInformation();
        HistoryItem save = new HistoryItem("REDO", redoneText);
        JSONConverter.updateHistory(save, "action_history.json");
        PageNavigator.refreshAllWindows();
    }
}
