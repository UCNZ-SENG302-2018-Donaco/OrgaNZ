package com.humanharvest.organz;

import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.Country;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

/**
 * The main class that runs the JavaFX GUI.
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

        State.init(DataStorageType.REST);
        /**
        // DEMO CLIENTS FOR MEMORY TESTING
        Client client = new Client("Thomas","Lives in Memory","Client", LocalDate.of(1998,02,02),
            1);
        client.setCountry(Country.NZ);
        client.setCurrentAddress("Within Local Memory 0 - 8196GB of it");
        client.setRegion("Within New Zealand");
        Client client1 = new Client("Jordan","Resides in Memory","Client",LocalDate.of(1990,01,1),1);
        client1.setCountry(Country.AD);
        client1.setRegion("Outside NZ");

        State.getClientManager().addClient(client);
        State.getClientManager().addClient(client1); **/

        if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }
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
