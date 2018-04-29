package seng302;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import seng302.Controller.MainController;
import seng302.State.State;
import seng302.Utilities.JSONConverter;
import seng302.Utilities.LoggerSetup;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

/**
 * The main class that runs the JavaFX GUI.
 * @author Dylan Carlyle, Jack Steel, Alex Tompkins, James Toohey
 * @version sprint 2.
 * date: 2018-03-22
 */
public class AppUI extends Application {

    private static Stage window;

    public static Stage getWindow() {
        return AppUI.window;
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     * @param stage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage stage) throws IOException {
        LoggerSetup.setup(Level.INFO);

        stage.setTitle("Organ Donor Management System");
        stage.setScene(createScene(loadMainPane(stage)));
        stage.show();

        State.init();

        // Loads the initial donor data from the save file, or creates it if it does not yet exist. //
        File saveFile = new File("savefile.json");
        JSONConverter.createEmptyJSONFileIfNotExists(saveFile);
        JSONConverter.loadFromFile(saveFile);
    }

    /**
     * Loads the main FXML. Sets up the page-switching PageNavigator. Loads the landing page as the initial page.
     * @param stage The stage to set the window to
     * @return The loaded pane.
     * @throws IOException Thrown if the pane could not be loaded.
     */
    private Pane loadMainPane(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = loader.load(getClass().getResourceAsStream(Page.MAIN.getPath()));
        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.setWindowContext(WindowContext.defaultContext());

        State.addMainController(mainController);

        PageNavigator.loadPage(Page.LANDING, mainController);

        return mainPane;
    }

    /**
     * Creates the main application scene.
     * @param mainPane The main application layout.
     * @return Returns the created scene.
     */
    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        // TODO Add CSS files for styling.
        // scene.getStylesheets().setAll(getClass().getResource("page.css").toExternalForm());
        return scene;
    }
}
