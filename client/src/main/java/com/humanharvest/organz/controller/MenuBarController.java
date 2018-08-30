package com.humanharvest.organz.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
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
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.AppUI;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.CacheManager;
import com.humanharvest.organz.utilities.exceptions.BadRequestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.views.ActionResponseView;

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
    public MenuItem staffListItem;
    public MenuItem createAdministratorItem;
    public MenuItem createClinicianItem;
    public MenuItem transplantRequestsItem;
    public MenuItem organsToDonateItem;
    public MenuItem viewClinicianItem;
    public MenuItem historyItem;
    public MenuItem cliItem;
    public MenuItem logOutItem;
    public MenuItem saveClientsItem;
    public MenuItem saveCliniciansItem;
    public MenuItem loadItem;
    public MenuItem undoItem;
    public MenuItem redoItem;
    public MenuItem closeItem;
    public MenuItem createClientItem;
    public MenuItem refreshCacheItem;
    public MenuItem settingsItem;

    public SeparatorMenuItem topSeparator;

    public MenuBar menuBar;

    public Menu filePrimaryItem;
    public Menu editPrimaryItem;
    public Menu clientPrimaryItem;
    public Menu organPrimaryItem;
    public Menu medicationsPrimaryItem;
    public Menu staffPrimaryItem;
    public Menu profilePrimaryItem;


    private Session session;

    /**
     * Gets the ActionInvoker from the current state.
     */
    public MenuBarController() {
        session = State.getSession();
    }

    @Override
    public void setup(MainController controller) {
        super.setup(controller);
        UserType userType = session.getLoggedInUserType();

        // Define what menus and menu items should be hidden

        // Menus/Menu items to hide from admins
        Menu[] menusHideFromAdmins = {medicationsPrimaryItem};
        MenuItem[] menuItemsHideFromAdmins = {viewClientItem, donateOrganItem, requestOrganItem, viewMedicationsItem,
                medicalHistoryItem, proceduresItem, viewClinicianItem};

        // Menus/Menu items to hide from clinicians
        Menu[] menusHideFromClinicians = {medicationsPrimaryItem, staffPrimaryItem};
        MenuItem[] menuItemsHideFromClinicians = {viewClientItem, donateOrganItem, requestOrganItem, viewMedicationsItem,
                medicalHistoryItem, proceduresItem, saveClientsItem, saveCliniciansItem, loadItem, settingsItem,
                staffListItem, createAdministratorItem, createClinicianItem, cliItem};

        // Menus/Menu items to hide from clinicians (or admins) viewing a client
        Menu[] menusHideFromClinViewClients = {staffPrimaryItem, profilePrimaryItem};
        MenuItem[] menuItemsHideFromClinViewClients = {saveClientsItem, saveCliniciansItem, loadItem, settingsItem,
                logOutItem, searchClientItem, createClientItem, transplantRequestsItem, organsToDonateItem,
                staffListItem, createAdministratorItem, createClinicianItem,
                viewClinicianItem, historyItem, cliItem, topSeparator};

        // Menus to hide from clients (aka all menus)
        Menu[] allMenus = {filePrimaryItem, editPrimaryItem, clientPrimaryItem, organPrimaryItem,
                medicationsPrimaryItem, staffPrimaryItem, profilePrimaryItem};

        // Hide the appropriate menus and menu items

        // Clinicians (or admins) viewing a client
        if ((userType == UserType.CLINICIAN || userType == UserType.ADMINISTRATOR)
                && windowContext.isClinViewClientWindow()) {
            hideMenus(menusHideFromClinViewClients);
            hideMenuItems(menuItemsHideFromClinViewClients);
        }

        // Admins
        else if (userType == UserType.ADMINISTRATOR) {
            hideMenus(menusHideFromAdmins);
            hideMenuItems(menuItemsHideFromAdmins);
        }

        // Clinicians
        else if (userType == UserType.CLINICIAN) {
            hideMenus(menusHideFromClinicians);
            hideMenuItems(menuItemsHideFromClinicians);
        }

        // Clients
        else if (userType == UserType.CLIENT) {
            hideMenus(allMenus);
        }

        refresh();
    }

    /**
     * Hides all the menu items in the passed-in array.
     * @param items The menu items to hide
     */
    private void hideMenuItems(MenuItem[] items) {
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
    private void hideMenus(Menu[] menus) {
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
    private void goToSearch() {
        PageNavigator.loadPage(Page.SEARCH, mainController);
    }

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
     * Redirects the GUI to the Organs To Donate page.
     */
    @FXML
    private void goToOrganstoDonate() {
        PageNavigator.loadPage(Page.ORGANS_TO_DONATE, mainController);
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
    private void goToCreateClient() {
        PageNavigator.loadPage(Page.CREATE_CLIENT, mainController);
    }

    /**
     * Redirects the GUI to the Admin command line page.
     */
    @FXML
    private void goToCommandLine() {
        PageNavigator.loadPage(Page.COMMAND_LINE, mainController);
    }

    @FXML
    private void goToSettings() {
        PageNavigator.loadPage(Page.ADMIN_CONFIG, mainController);
    }

    /**
     * Opens a save file dialog to choose where to save all clients in the system to a file.
     */
    @FXML
    private void saveClients() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Clients File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(mainController.getStage());
            if (file != null) {
                try (FileOutputStream output = new FileOutputStream(file)) {
                    output.write(State.getFileResolver().exportClients());
                }

                Notifications.create()
                        .title("Saved Data")
                        .text(String.format("Successfully saved all clients to file '%s'.", file.getName()))
                        .showInformation();

                State.setUnsavedChanges(false);
                PageNavigator.refreshAllWindows();
            }
        } catch (URISyntaxException | IOException e) {
            PageNavigator.showAlert(AlertType.WARNING, "Save Failed", ERROR_SAVING_MESSAGE);
            LOGGER.log(Level.SEVERE, ERROR_SAVING_MESSAGE, e);
        }
    }

    /**
     * Opens a saveClinicians file dialog to choose where to save all clinicians in the system to a file.
     */
    @FXML
    private void saveClinicians() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Clinicians File");
            fileChooser.setInitialDirectory(
                    new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getParent().toString())
            );
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));
            File file = fileChooser.showSaveDialog(mainController.getStage());
            if (file != null) {
                try (FileOutputStream output = new FileOutputStream(file)) {
                    output.write(State.getFileResolver().exportClinicians());
                }

                Notifications.create()
                        .title("Saved Data")
                        .text(String.format("Successfully saved all clinicians to file '%s'.", file.getName()))
                        .showInformation();

                State.setUnsavedChanges(false);
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
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Clients File");
            try {
                fileChooser.setInitialDirectory(
                        new File(Paths.get(AppUI.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                                .getParent().toString()));
            } catch (URISyntaxException e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
            }
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "JSON/CSV files (*.json, *.csv)",
                    "*.json", "*.csv"));
            File file = fileChooser.showOpenDialog(mainController.getStage());

            if (file != null) {
                String format = getFileExtension(file.getName());

                try {
                    String message = State.getFileResolver().importClients(
                            Files.readAllBytes(file.toPath()), format);

                    LOGGER.log(Level.INFO, message);

                    Notifications.create()
                            .title("Loaded Clients")
                            .text(message)
                            .showInformation();

                    mainController.resetWindowContext();
                    PageNavigator.loadPage(Page.LANDING, mainController);

                } catch (IllegalArgumentException exc) {
                    PageNavigator.showAlert(AlertType.ERROR, "Load Failed", exc.getMessage());
                } catch (IOException | BadRequestException exc) {
                    PageNavigator.showAlert(AlertType.ERROR, "Load Failed",
                            String.format("An error occurred when loading from file: '%s'\n%s",
                                    file.getName(), exc.getMessage()));
                }
            }
        }
    }

    /**
     * Returns the file extension of the given file name string (in lowercase). The file extension is defined as the
     * characters after the last "." in the file name.
     * @param fileName The file name string.
     * @return The file extension of the given file name.
     */
    private static String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex >= 0) {
            return fileName.substring(lastIndex + 1).toLowerCase();
        } else {
            return "";
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
        State.clearMainControllers();
        State.addMainController(mainController);
        mainController.resetWindowContext();
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    @FXML
    private void refreshCache() {

        // Generate initial alert popup
        String alertTitle = "Refreshing cache...";
        Alert alert = PageNavigator.generateAlert(AlertType.INFORMATION, alertTitle, "The cache is refreshing.");
        alert.show();

        Task<List<String>> task = new Task<List<String>>() {
            @Override
            public List<String> call() {
                CacheManager.INSTANCE.refreshCachedData();
                return new ArrayList<>();
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
        ActionResponseView responseView = State.getActionResolver().getUndo();
        undoItem.setDisable(!responseView.isCanUndo());
        redoItem.setDisable(!responseView.isCanRedo());
    }

    /**
     * Undoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void undo() {
        ActionResponseView responseView = State.getActionResolver().executeUndo(State.getRecentEtag());
        Notifications.create().title("Undo").text(responseView.getResultText()).showInformation();
        PageNavigator.refreshAllWindows();
    }

    /**
     * Redoes the most recent action performed in the system, and refreshes the current page to reflect the change.
     */
    @FXML
    private void redo() {
        ActionResponseView responseView = State.getActionResolver().executeRedo(State.getRecentEtag());
        Notifications.create().title("Redo").text(responseView.getResultText()).showInformation();
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
            Alert unsavedAlert = PageNavigator
                    .generateAlert(AlertType.WARNING, "Do you want to save the changes you have made?",
                            "Your changes will be lost if you do not save them.");
            ButtonType dontSave = new ButtonType("Don't Save");
            ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            ButtonType save = new ButtonType("Save");
            unsavedAlert.getButtonTypes().setAll(dontSave, cancel, save);

            Optional<ButtonType> result = unsavedAlert.showAndWait();
            if (result.isPresent() && result.get() == dontSave) {
                exit();
            } else if (result.isPresent() && result.get() == save) {
                saveClients();
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
    }
}
