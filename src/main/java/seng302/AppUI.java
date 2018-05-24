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
        return window;
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     * @param primaryStage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup(Level.INFO);

        primaryStage.setTitle("Organ Client Management System");
        primaryStage.setScene(createScene(loadMainPane(primaryStage)));
        primaryStage.show();

        primaryStage.setMinHeight(639);
        primaryStage.setMinWidth(1016);

        State.init();

        // Loads the initial client data from the save file, or creates it if it does not yet exist. //
        /*
        File saveFile = new File("savefile.json");
        JSONConverter.createEmptyJSONFileIfNotExists(saveFile);
        JSONConverter.loadFromFile(saveFile);
        */
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
    private static Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        addCss(scene);
        return scene;
    }

    public static void addCss(Scene scene) {
        scene.getStylesheets().add(AppUI.class.getResource("/css/validation.css").toExternalForm());
    }
}
