package seng302.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import seng302.Actions.ActionInvoker;
import seng302.AppUI;
import seng302.Client;
import seng302.HistoryItem;
import seng302.State.Session;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.CacheManager;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;

import org.controlsfx.control.Notifications;

/**
 * Controller for the sidebar pane imported into every page in the main part of the GUI.
 */
public class MenuBarController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(MenuBarController.class.getName());

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
    public MenuItem undoItem;
    public MenuItem redoItem;
    public MenuItem closeItem;
    public MenuItem createClientItem;
    public MenuItem refreshCacheItem;

    public SeparatorMenuItem topSeparator;

    public MenuBar menuBar;

    public Menu clientPrimaryItem;
    public Menu organPrimaryItem;
    public Menu medicationsPrimaryItem;
    public Menu staffPrimaryItem;
    public Menu transplantsPrimaryItem;
    public Menu profilePrimaryItem;
    public Menu filePrimaryItem;
    public Menu administrationPrimaryItem;

    private ActionInvoker invoker;
    private Session session;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public MenuBarController() {
        invoker = State.getInvoker();
        session = State.getSession();
    }

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        UserType userType = session.getLoggedInUserType();

        Menu viewAllMenus[] = {clientPrimaryItem, organPrimaryItem, medicationsPrimaryItem, staffPrimaryItem,
                transplantsPrimaryItem, profilePrimaryItem, filePrimaryItem};

        Menu viewAdminMenu[] = {staffPrimaryItem, transplantsPrimaryItem, filePrimaryItem,
                profilePrimaryItem};

        Menu clinicianWindowMenu[] = {staffPrimaryItem, medicationsPrimaryItem, };
        MenuItem clinicianWindowMenuItems[] = {organPrimaryItem, viewClientItem, medicationsPrimaryItem};

        Menu clinViewClientMenu[] = {staffPrimaryItem, profilePrimaryItem, transplantsPrimaryItem};
        MenuItem clinViewClientMenuItem[] = {searchClientItem, createClientItem};

        if (userType == UserType.CLINICIAN) {

            if (windowContext.isClinViewClientWindow()) {
                removeAdminMenuItems();
                hideMenus(clinViewClientMenu);
                hideMenuItems(clinViewClientMenuItem);

            } else if (!windowContext.isClinViewClientWindow()) {
                removeAdminMenuItems();
                hideMenus(clinicianWindowMenu);
                hideMenuItems(clinicianWindowMenuItems);
                // staff primary item - seng302.Controller.Administrator.StaffListController.lambda$null$1(StaffListController.java:89)
            }
        }
        if (userType == UserType.ADMINISTRATOR) {

            if (windowContext.isClinViewClientWindow()){
                hideMenuItem(profilePrimaryItem);
                hideMenuItem(staffPrimaryItem);
                hideMenuItem(createClientItem);
                hideMenuItem(viewAdministratorItem);
            } else if (!windowContext.isClinViewClientWindow()) {
                hideMenuItem(organPrimaryItem);
                hideMenuItem(medicationsPrimaryItem);
                hideMenuItem(viewClinicianItem);
                hideMenuItem(viewClientItem);
                hideMenuItem(viewAdministratorItem);
            }
        }
        if (userType == UserType.CLIENT == true) {
            hideMenus(viewAllMenus);
        }
        closeItem.setDisable(!windowContext.isClinViewClientWindow());
        undoItem.setDisable(!invoker.canUndo());
        redoItem.setDisable(!invoker.canRedo());
    }


    /**
     * Removes all menu items and menus that only admins should have.
     */
    private void removeAdminMenuItems() {
        // Remove administrator file rights.
        hideMenuItem(createAdministratorItem);
        topSeparator.setVisible(false);
        hideMenuItem(saveItem);
        hideMenuItem(loadItem);
        hideMenuItem(viewAdministratorItem);
        hideMenuItem(cliItem);
        hideMenu(administrationPrimaryItem);

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
     * Hides all the menu items in the passed-in array.
     * @param items The menu items to hide
     */
    private void hideMenuItems(MenuItem items[]) {
        for (MenuItem menuItem : items) {
            hideMenuItem(menuItem);
        }
    }

    /**
     * Hides the menu item from the menu.
     * @param menuItem the menu item to hide
     */
    private void hideMenuItem(MenuItem menuItem) {
        menuItem.setVisible(false);
        //menuItem.setManaged(false);
    }

    /**
     * Hides all the menus in the passed-in array.
     * @param menus The menus to hide
     */
    private void hideMenus(Menu menus[]) {
        for (Menu menu : menus) {
            hideMenu(menu);
        }
    }

    /**
     * Hides the Primary menu from the menu bar.
     * @param menu the menu to hide
     */
    private void hideMenu(Menu menu) {
        menu.setVisible(false);
        //menu.setManaged(false);
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
    private void goToSearch() { PageNavigator.loadPage(Page.SEARCH, mainController);}

    /**
     * Redirects the GUI to the Staff list page.
     */
    @FXML
    private void goToStaffList() {
        PageNavigator.loadPage(Page.STAFF_LIST, mainController);
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

    /**
     * Redirects the GUI to the Create administrator page.
     */
    @FXML
    private void goToCreateAdmin() {
        PageNavigator.loadPage(Page.CREATE_ADMINISTRATOR, mainController);
    }

    /**
     * Redirects the GUI to the Create clinician page.
     */
    @FXML
    private void goToCreateClinician() {
        PageNavigator.loadPage(Page.CREATE_CLINICIAN, mainController);
    }

    /**
     * Redirects the GUI to the Create clinician page.
     */
    @FXML
    private void goToCreateClient() {PageNavigator.loadPage(Page.CREATE_CLIENT, mainController);}

    /**
     * Redirects the GUI to the Admin command line page.
     */
    @FXML
    private void goToCommandLine() {
        PageNavigator.loadPage(Page.COMMAND_LINE, mainController);
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

    @FXML
    private void refreshCache() {

        // Generate initial alert popup
        String alertTitle = "Refreshing cache...";
        Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION, alertTitle, "The cache is refreshing.");
        alert.show();

        Task<List<String>> task = new Task<List<String>>() {
            @Override
            public List<String> call() throws IOException {
                CacheManager.INSTANCE.refreshCachedData();
                return new ArrayList<String>();
            }
        };

        task.setOnSucceeded(e -> {
            String title = "Cache refreshed.";
            alert.setHeaderText(title);
            alert.setTitle(title);
            alert.setContentText("The cache has been refreshed.");
        });

        task.setOnFailed(e -> {
            alert.setAlertType(AlertType.ERROR);
            String title = "Error refreshing cache.";
            alert.setHeaderText(title);
            alert.setTitle(title);
            alert.setContentText("Error refreshing cache. Please try again later.");

        });

        new Thread(task).start();
    }

    /**
     * Refreshes the undo/redo buttons based on if there are changes to be made
     */
    public void refresh() {
        undoItem.setDisable(!invoker.canUndo());
        redoItem.setDisable(!invoker.canRedo());
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

    /**
     * Closes the current window
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        stage.close();
    }

    /**
     * Prompts the user to save their changes if there are changes unsaved, then exits the program.
     */
    @FXML
    private void quitProgram() {
        if (State.isUnsavedChanges()) {
            Alert unsavedAlert = PageNavigator.generateAlert(AlertType.WARNING, "Do you want to save the changes you have made?",
                    "Your changes will be lost if you do not save them.");
            ButtonType dontSave = new ButtonType("Don't Save");
            ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            ButtonType save = new ButtonType("Save");
            unsavedAlert.getButtonTypes().setAll(dontSave, cancel, save);

            Optional<ButtonType> result = unsavedAlert.showAndWait();
            if (result.get() == dontSave) {
                exit();
            } else if (result.get() == save) {
                save();
                exit();
            } else {
                unsavedAlert.hide();
            }

        } else {
            exit();
        }

    }

    /**
     * Exit program.
     */
    private void exit() {
        Platform.exit();
        System.exit(0);
    }
}
