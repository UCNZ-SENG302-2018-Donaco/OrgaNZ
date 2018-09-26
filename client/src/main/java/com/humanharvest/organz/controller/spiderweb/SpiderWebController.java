package com.humanharvest.organz.controller.spiderweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.PhysicsHandler;
import com.humanharvest.organz.touch.PointUtils;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext;

/**
 * The Spider web controller which handles everything to do with displaying panes in the spider web stage.
 */
public class SpiderWebController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(SpiderWebController.class.getName());
    private static final double RADIUS = 300;

    private final Client client;

    private final List<MainController> previouslyOpenWindows = new ArrayList<>();
    private final List<OrganWithRecipients> organWithRecipientsList = new ArrayList<>();
    private final Pane rootPane;
    private final Pane canvas;
    private Pane deceasedDonorPane;

    public SpiderWebController(Client viewedClient) {
        client = viewedClient;
        client.setDonatedOrgans(State.getClientResolver().getDonatedOrgans(client));
        State.setSpiderwebDonor(client);

        rootPane = MultitouchHandler.getRootPane();
        rootPane.getChildren().clear();
        canvas = new Pane();
        rootPane.getChildren().add(canvas);

        // Close existing windows, but save them for later
        // Copy to a new list to prevent concurrent modification
        Iterable<MainController> toClose = new ArrayList<>(State.getMainControllers());
        for (MainController mainController : toClose) {
            mainController.closeWindow();
            previouslyOpenWindows.add(mainController);
        }

        // Need to do this to be able to refresh the spider web
        mainController = new MainController();
        mainController.setStage(new Stage());
        mainController.setPane(new Pane());
        mainController.setSubController(this);
        mainController.setWindowContext(WindowContext.defaultContext());
        State.addMainController(mainController);

        // Setup spider web physics
        MultitouchHandler.setPhysicsHandler(new SpiderPhysicsHandler(MultitouchHandler.getRootPane()));

        // Setup page
        setupButtons();
        displayDonatingClient();
        displayOrgans();
    }

    /**
     * Sets a node's position using an {@link Affine} transform. The node must have an {@link FocusArea} for its
     * {@link Node#getUserData()}.
     *
     * @param node The node to apply the transform to. Must have a focusArea
     * @param x The x translation
     * @param y The y translation
     * @param angle The angle to rotate (degrees)
     * @param scale The scale to apply to both x and y
     */
    private static void setPositionUsingTransform(Node node, double x, double y, double angle, double scale) {
        FocusArea focusArea = (FocusArea) node.getUserData();

        Point2D centre = PointUtils.getCentreOfNode(node);

        Affine transform = new Affine();
        transform.append(new Translate(x, y));
        transform.append(new Scale(scale, scale));
        transform.append(new Rotate(angle, centre.getX(), centre.getY()));
        focusArea.setTransform(transform);

        node.setCacheHint(CacheHint.QUALITY);
    }

    /**
     * Setup the basic buttons in the top left corner
     */
    private void setupButtons() {
        Button exitButton = new Button("Exit Spider Web");
        exitButton.setOnAction(__ -> closeSpiderWeb());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(__ -> resetLayout());

        HBox buttons = new HBox();
        buttons.setSpacing(10);
        buttons.getChildren().addAll(exitButton, resetButton);
        canvas.getChildren().add(buttons);
    }

    /**
     * Loads a window for each non expired organ.
     */
    private void displayOrgans() {
        List<DonatedOrgan> donatedOrgans = client.getDonatedOrgans();
        Bounds donorBounds = deceasedDonorPane.getBoundsInParent();
        double centreX = donorBounds.getMinX() + donorBounds.getWidth() / 2;
        double centreY = donorBounds.getMinY() + donorBounds.getHeight() / 2;
        double angleSize = (Math.PI * 2) / donatedOrgans.size();

        for (int i = 0; i < donatedOrgans.size(); i++) {
            DonatedOrgan donatedOrgan = donatedOrgans.get(i);

            double xPos = centreX + RADIUS * Math.sin(angleSize * i);
            double yPos = centreY + RADIUS * Math.cos(angleSize * i);
            double angle = 360.01 - Math.toDegrees(angleSize * i);

            addOrganNode(donatedOrgan, xPos, yPos, angle);
        }
    }

    private void addOrganNode(DonatedOrgan organ, double xPos, double yPos, double rotation) {
        OrganWithRecipients organWithRecipients = new OrganWithRecipients(organ, deceasedDonorPane,
                canvas);

        organWithRecipientsList.add(organWithRecipients);

        setPositionUsingTransform(organWithRecipients.getOrganPane(), xPos, yPos, rotation, 1);
    }

    private void displayDonatingClient() {
        MainController newMain = PageNavigator.openNewWindow(200, 320);
        PageNavigator.loadPage(Page.DECEASED_DONOR_OVERVIEW, newMain);

        deceasedDonorPane = newMain.getPane();

        FocusArea deceasedDonorFocus = (FocusArea) deceasedDonorPane.getUserData();
        deceasedDonorFocus.setTranslatable(false);
        deceasedDonorFocus.setCollidable(true);

        Bounds bounds = deceasedDonorPane.getBoundsInParent();
        double centerX = (Screen.getPrimary().getVisualBounds().getWidth() - bounds.getWidth()) / 2;
        double centerY = (Screen.getPrimary().getVisualBounds().getHeight() - bounds.getHeight()) / 2;
        setPositionUsingTransform(deceasedDonorPane, centerX, centerY, 0, 0.6);

        // We need to register this so when the deceasedDonorPane gets properly positioned it updates the lines
        deceasedDonorPane.boundsInLocalProperty().addListener(
                (__, ___, ____) -> organWithRecipientsList.forEach(
                        organ -> organ.setDonorConnectorStart(deceasedDonorPane.getBoundsInParent())));
    }

    private void resetLayout() {
        Bounds donorBounds = deceasedDonorPane.getBoundsInParent();
        double centreX = donorBounds.getMinX() + donorBounds.getWidth() / 2;
        double centreY = donorBounds.getMinY() + donorBounds.getHeight() / 2;
        double angleSize = (Math.PI * 2) / organWithRecipientsList.size();

        for (int i = 0; i < organWithRecipientsList.size(); i++) {
            OrganWithRecipients organWithRecipients = organWithRecipientsList.get(i);

            double xPos = centreX + RADIUS * Math.sin(angleSize * i);
            double yPos = centreY + RADIUS * Math.cos(angleSize * i);
            double angle = 360.01 - Math.toDegrees(angleSize * i);

            setPositionUsingTransform(organWithRecipients.getOrganPane(), 0, 0, 0, 0);
            setPositionUsingTransform(organWithRecipients.getOrganPane(), xPos, yPos, angle, 1);
        }
    }

    @FXML
    private void closeSpiderWeb() {
        MultitouchHandler.setPhysicsHandler(new PhysicsHandler(MultitouchHandler.getRootPane()));

        // Close all windows for the spider web and clear
        // Copy to a new list to prevent concurrent modification
        List<MainController> toClose = new ArrayList<>(State.getMainControllers());
        toClose.forEach(MainController::closeWindow);
        rootPane.getChildren().clear();

        // We need to close the Timeline to clear resources
        organWithRecipientsList.forEach(OrganWithRecipients::closeRefresher);

        // Open all the previously open windows again
        previouslyOpenWindows.forEach(MainController::showWindow);
    }

    @Override
    public void refresh() {

        client.setDonatedOrgans(State.getClientResolver().getDonatedOrgans(client));
        for (OrganWithRecipients page : organWithRecipientsList) {
            Optional<DonatedOrgan> newOrgan = client.getDonatedOrgans().stream()
                    .filter(organ -> organ.getOrganType() == page.getOrgan().getOrganType())
                    .findFirst();
            if (!newOrgan.isPresent()) {
                Notifications.create()
                        .title("Server Error")
                        .text("An error occurred when trying to schedule the transplant.")
                        .showError();
            } else {
                page.refresh(newOrgan.get());
            }
        }
    }
}
