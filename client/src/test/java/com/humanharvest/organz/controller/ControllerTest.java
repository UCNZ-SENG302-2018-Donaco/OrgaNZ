package com.humanharvest.organz.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.humanharvest.organz.GUICategory;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.ReflectionUtils;
import com.humanharvest.organz.utilities.view.Page;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.springframework.web.client.RestTemplate;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;

@Category(GUICategory.class)
public abstract class ControllerTest extends ApplicationTest {

    protected MainController mainController;
    protected SubController pageController;
    protected RestTemplate mockRestTemplate;
    protected Node pageNode;

    @BeforeClass
    public static void initialise() {
        String disableHeadless = System.getProperty("java.disable.headless");
        if (disableHeadless == null || disableHeadless.isEmpty()) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }
    }

    /**
     * Get the top modal window.
     *
     * @return the top modal window
     */
    protected static Stage getTopModalStage() {
        // Get a list of windows but ordered from top[0] to bottom[n] ones.
        List<Window> allWindows = new ArrayList<>(new FxRobot().robotContext().getWindowFinder().listWindows());
        Collections.reverse(allWindows);

        // Return the first found modal window.
        return (Stage) allWindows
                .stream()
                .filter(window -> window instanceof Stage)
                .findFirst()
                .orElse(null);
    }

    protected static <T, Y> Y setPrivateField(Class<T> clazz, String fieldName, Y newValue)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Y result = (Y) field.get(null);
        field.set(null, newValue);
        return result;
    }

    protected static <T, Y> void setPrivateField(Class<T> clazz, T instance, String fieldName, Y newValue)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, newValue);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load main pane and controller
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(Page.MAIN.getPath()));
        Pane mainPane = mainLoader.load();
        mainController = mainLoader.getController();

        mockRestTemplate = mock(RestTemplate.class);
        State.setRestTemplate(mockRestTemplate);
        initState();
        State.addMainController(mainController);

        // Load page's node and controller
        FXMLLoader pageLoader = new FXMLLoader(getClass().getResource(getPage().getPath()));
        pageNode = pageLoader.load();
        pageController = pageLoader.getController();

        // Setup main controller and set stage's scene to its main pane, then show stage
        mainController.setStage(stage);
        stage.setScene(new Scene(mainPane));
        stage.show();

        // Setup both main and page controller
        pageController.setup(mainController);
        mainController.setPage(getPage(), pageNode);
        mainController.setSubController(pageController);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // Cleans up memory - Needed due to TestFX memory leaks
        mainController = null;
        pageController = null;
        mockRestTemplate = null;
        pageNode = null;
        setPrivateField(FxRobot.class, this, "context", null);
    }

    @After
    public void killAllWindows() {
        Stage stage = getTopModalStage();
        State.reset();
        if (stage != null) {
            Parent parent = stage.getScene().getRoot();
            if (parent instanceof DialogPane) {
                Platform.runLater(stage::close);
            }
        }
    }


    /**
     * Asserts that header is expectedHeader and content contains expectedContent.
     */
    protected static void alertDialogHasHeaderAndContainsContent(String expectedHeader, String expectedContent) {
        Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertThat(dialogPane.getContentText(), containsString(expectedContent));
    }
    /**
     * Asserts that header is expectedHeader and content contains each String in expectedContents.
     */
    protected static void alertDialogHasHeaderAndContainsContents(String expectedHeader, String[] expectedContents) {
        Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        for (String expectedContent : expectedContents) {
            assertThat(dialogPane.getContentText(), containsString(expectedContent));
        }
    }

    protected abstract Page getPage();

    protected abstract void initState() throws Exception;
}
