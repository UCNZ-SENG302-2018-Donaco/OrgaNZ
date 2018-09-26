package com.humanharvest.organz;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.ProjectionHelper;
import com.humanharvest.organz.controller.clinician.StaffLoginController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.utilities.LoggerSetup;
import com.humanharvest.organz.utilities.ReflectionUtils;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorStandard;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;
import com.humanharvest.organz.utilities.view.WindowContext;

import com.sun.javafx.css.StyleManager;
import com.sun.prism.impl.PrismSettings;
import org.tuiofx.TuioFX;
import org.tuiofx.internal.base.TuioFXCanvas;

/**
 * The main class that runs the JavaFX GUI.
 */
public class AppUI extends Application {

    static {
        // Must be done here, since getting the property happens before the class is created
        if (System.getProperty("prism.maxvram") == null) {
            ReflectionUtils.setStaticField(PrismSettings.class, "maxVram", 2L * 1024 * 1024 * 1024); //2GB
        }
        TuioFX.enableJavaFXTouchProperties();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Loads a backdrop page.
     */
    private static void loadBackPane(Pane rootPane) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane backPane = loader.load(PageNavigatorTouch.class.getResourceAsStream(Page.BACKDROP.getPath()));

        rootPane.getChildren().add(backPane);
    }

    /**
     * Loads the landing page as the initial page.
     */
    private static void loadTouchMainPane() {
        MainController mainController = PageNavigator.openNewWindow();
        mainController.setWindowContext(WindowContext.defaultContext());
        PageNavigator.loadPage(Page.LANDING, mainController);
    }

    private static void addCss(Scene scene) {
        scene.getStylesheets().add(AppUI.class.getResource("/css/validation.css").toExternalForm());
    }

    /**
     * Initialises the touch components, namely the MultitouchHandler and various panes.
     */
    private static void startTouch(Stage primaryStage) throws IOException {
        Pane root = new TuioFXCanvas();
        Scene scene = new Scene(root);

        loadBackPane(root);
        MultitouchHandler.initialise(root);

        loadTouchMainPane();

        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);
        primaryStage.setOnCloseRequest(event -> {
            ProjectionHelper.stageClosing();
            MultitouchHandler.stageClosing();
        });
    }

    /**
     * Starts the JavaFX GUI. Sets up the main stage and initialises the state of the system.
     * Loads from the save file or creates one if one does not yet exist.
     *
     * @param primaryStage The stage given by the JavaFX launcher.
     * @throws IOException If the save file cannot be loaded/created.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        LoggerSetup.setup("organz.log", Level.INFO);
        LoggerSetup.enableConsole(Level.WARNING);

        processArguments();

        State.init(DataStorageType.REST);
        State.setPrimaryStage(primaryStage);
        StyleManager.getInstance().addUserAgentStylesheet("/css/validation.css");

        Map<String, String> parameters = getParameters().getNamed();

        if (parameters.containsKey("host")) {
            State.setBaseUri(parameters.get("host"));
        } else if (System.getenv("HOST") != null) {
            State.setBaseUri(System.getenv("HOST"));
        }

        primaryStage.setTitle("Organ Donor Management System");

        switch (State.getUiType()) {
            case STANDARD:
                primaryStage.setScene(new Scene(loadStandardMainPane(primaryStage)));
                addCss(primaryStage.getScene());
                break;

            case TOUCH:
                startTouch(primaryStage);
                break;

            default:
                throw new UnsupportedOperationException("Unknown ui type");
        }

        primaryStage.show();

        primaryStage.setMinHeight(639);
        primaryStage.setMinWidth(1016);

        // Only enable projection helper on touch screen
        if (MultitouchHandler.getRootPane() != null) {
            ProjectionHelper.initialise(MultitouchHandler.getRootPane());
        }

        // Skips login page if arguments contains --login & --password
        if (parameters.containsKey("login")) {
            StaffLoginController.handleSignIn(parameters.get("login"),
                    parameters.getOrDefault("password", ""),
                    State.getMainControllers().get(0));
        }
    }

    /**
     * Process the various command line and environmental arguments.
     */
    private void processArguments() {

        getArgument("host").ifPresent(State::setBaseUri);

        Optional<String> uiType = getArgument("ui");
        if (uiType.isPresent() && "touch".equalsIgnoreCase(uiType.get())) {
            State.setUiType(State.UiType.TOUCH);
            PageNavigator.setPageNavigator(new PageNavigatorTouch());

            // Instead of tuioFX.enableMTWidgets(true)
            // We set our own stylesheet that contains less style changes but still loads
            // the skins required for multi touch
            Application.setUserAgentStylesheet("MODENA");
            StyleManager.getInstance().addUserAgentStylesheet("/css/multifocus.css");
            StyleManager.getInstance().addUserAgentStylesheet("/css/touch.css");
        } else {
            State.setUiType(State.UiType.STANDARD);
            PageNavigator.setPageNavigator(new PageNavigatorStandard());
        }
    }

    /**
     * Returns an the value of an argument, or empty if non exist. Will do a case insensative comparison, and look in
     * both program arguments and environmental variables.
     */
    private Optional<String> getArgument(String argument) {
        Map<String, String> parameters = getParameters().getNamed();

        for (Entry<String, String> parameter : parameters.entrySet()) {
            if (parameter.getKey().equalsIgnoreCase(argument)) {
                return Optional.of(parameter.getValue());
            }
        }

        for (Entry<String, String> environment : System.getenv().entrySet()) {
            if (environment.getKey().equalsIgnoreCase(argument)) {
                return Optional.of(environment.getValue());
            }
        }

        return Optional.empty();
    }

    /**
     * Loads the main FXML. Sets up the page-switching PageNavigator. Loads the landing page as the initial page.
     *
     * @param stage The stage to set the window to
     * @return The loaded pane.
     * @throws IOException Thrown if the pane could not be loaded.
     */
    private Pane loadStandardMainPane(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = loader.load(getClass().getResourceAsStream(Page.MAIN.getPath()));
        MainController mainController = loader.getController();
        mainController.setStage(stage);
        mainController.setPane(mainPane);
        mainController.setWindowContext(WindowContext.defaultContext());
        State.addMainController(mainController);
        PageNavigator.loadPage(Page.LANDING, mainController);

        return mainPane;
    }
}
