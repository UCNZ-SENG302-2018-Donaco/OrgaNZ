package com.humanharvest.organz.controller.spiderweb;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.controller.components.ExpiryBarUtils;
import com.humanharvest.organz.controller.components.PotentialRecipientCell;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.PhysicsHandler;
import com.humanharvest.organz.touch.PointUtils;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;

/**
 * The Spider web controller which handles everything to do with displaying panes in the spider web stage.
 */
public class SpiderWebController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(SpiderWebController.class.getName());
    private static final DurationFormat durationFormat = DurationFormat.X_HRS_Y_MINS_SECS;
    private static final double LABEL_OFFSET = 50.0;

    private static final ColorAdjust OVERRIDDEN_COLOR = new ColorAdjust(0, 0, -0.6, -0.6);
    private static final ColorAdjust EXPIRED_COLOR = new ColorAdjust(0, 0, -0.4, -0.4);

    private final Client client;

    private final List<MainController> previouslyOpenWindows = new ArrayList<>();
    private final Pane canvas;
    private Pane deceasedDonorPane;
    private final List<Pane> organNodes = new ArrayList<>();
    private final List<ListView<Client>> matchesLists = new ArrayList<>();

    public SpiderWebController(Client viewedClient) {
        client = viewedClient;
        client.setDonatedOrgans(State.getClientResolver().getDonatedOrgans(client));
        State.setSpiderwebDonor(client);

        canvas = MultitouchHandler.getCanvas();
        canvas.getChildren().clear();

        // Close existing windows, but save them for later
        MultitouchHandler.setPhysicsHandler(new SpiderPhysicsHandler(MultitouchHandler.getRootPane()));
        for (MainController mainController : State.getMainControllers()) {
            mainController.closeWindow();
            previouslyOpenWindows.add(mainController);
        }

        Button exitButton = new Button("Exit Spider Web");
        canvas.getChildren().add(exitButton);
        exitButton.setOnAction(event -> closeSpiderWeb());

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

    private static void updateConnectorText(Text durationText, DonatedOrgan donatedOrgan, Line line) {
        // Set the text
        durationText.setText(ExpiryBarUtils.getDurationString(donatedOrgan, durationFormat));

        // Remove the old translation
        durationText.getTransforms().removeIf(Affine.class::isInstance);

        Affine trans = new Affine();

        // Translate the text to the left by at least LABEL_OFFSET and more if the line is a large width
        // Also move it up by 5 pixels to put it just above the line
        double xWidth = line.getStartX() - line.getEndX();
        double yWidth = line.getStartY() - line.getEndY();
        double lineWidth = Math.sqrt(Math.pow(xWidth, 2) + Math.pow(yWidth, 2));
        trans.prepend(new Translate(Math.max(LABEL_OFFSET, lineWidth * 0.2), -5));

        // Rotate the text by the angle of the line
        double angle = getAngle(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        trans.prepend(new Rotate(angle));

        // Apply the new transformation
        durationText.getTransforms().add(trans);

        // Translate the text to the end of the line (then the above transforms take effect)
        durationText.setTranslateX(line.getEndX());
        durationText.setTranslateY(line.getEndY());
    }

    private static void updateConnector(DonatedOrgan donatedOrgan, Line line, Text durationText, Pane organPane) {
        OrganState organState = donatedOrgan.getState();
        switch (organState) {
            case OVERRIDDEN:
                line.setStroke(Color.BLACK);
                organPane.setEffect(OVERRIDDEN_COLOR);
                break;
            case EXPIRED:
                line.setStroke(ExpiryBarUtils.darkGreyColour);
                organPane.setEffect(EXPIRED_COLOR);
                break;
            case NO_EXPIRY:
                line.setStroke(ExpiryBarUtils.noExpiryGreenColour);
                organPane.setEffect(null);
                break;
            case CURRENT:
                LinearGradient linearGradient = ExpiryBarUtils.getLinearGradient(
                        donatedOrgan.getProgressDecimal(), donatedOrgan.getFullMarker(),
                        line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

                line.setStroke(linearGradient);
                organPane.setEffect(null);
                break;
        }
        updateConnectorText(durationText, donatedOrgan, line);
    }

    private static double getAngle(double x1, double y1, double x2, double y2) {
        double angle = Math.toDegrees(Math.atan2(y1 - y2, x1 - x2));
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    /**
     * Loads a window for each non expired organ.
     */
    private void displayOrgans() {
        for (DonatedOrgan organ : client.getDonatedOrgans()) {
            addOrganNode(organ);
        }
        layoutOrganNodes(300);
    }

    private void addOrganNode(DonatedOrgan organ) {
        State.setOrganToDisplay(organ);
        MainController newMain = PageNavigator.openNewWindow(80, 80);
        PageNavigator.loadPage(Page.ORGAN_IMAGE, newMain);
        newMain.getStyles().clear();
        Pane organPane = newMain.getPane();
        FocusArea organFocus = (FocusArea) organPane.getUserData();
        organFocus.setScalable(false);
        organFocus.setCollidable(true);
        organNodes.add(organPane);
        // Double click to override and organ or to unoverride
        organPane.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && !organ.hasExpired()) {
                if (organ.getOverrideReason() == null) {
                    final String reason = "Manually Overridden by Doctor using WebView";
                    State.getClientResolver().manuallyOverrideOrgan(organ, reason);
                    organ.manuallyOverride(reason);

                } else {
                    State.getClientResolver().cancelManualOverrideForOrgan(organ);
                    organ.cancelManualOverride();
                }
            }
        });
        // Create the line
        Line connector = new Line();
        connector.setStrokeWidth(4);
        Text durationText = new Text(ExpiryBarUtils.getDurationString(organ, durationFormat));

        // Create matches list
        ListView<Client> matchesList = createMatchesList(organ);

        // Redraws lines when organs or donor pane is moved
        deceasedDonorPane.localToParentTransformProperty().addListener((observable, oldValue, newValue) -> {
            Bounds bounds = deceasedDonorPane.getBoundsInParent();
            connector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
            connector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
            updateConnector(organ, connector, durationText, organPane);
        });
        organPane.localToParentTransformProperty().addListener((observable, oldValue, newValue) -> {
            Bounds bounds = organPane.getBoundsInParent();
            connector.setEndX(bounds.getMinX() + bounds.getWidth() / 2);
            connector.setEndY(bounds.getMinY() + bounds.getHeight() / 2);
            updateConnector(organ, connector, durationText, organPane);
            updateMatchesListPosition(matchesList, newValue, bounds);
        });

        canvas.getChildren().add(0, connector);
        canvas.getChildren().add(0, durationText);
        canvas.getChildren().add(matchesList);

        Bounds bounds = deceasedDonorPane.getBoundsInParent();
        connector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
        connector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
        updateConnector(organ, connector, durationText, organPane);

        // Attach timer to update connector each second (for time until expiration)
        final Timeline clock = new Timeline(new KeyFrame(
                javafx.util.Duration.seconds(1),
                event -> {
                    updateConnector(organ, connector, durationText, organPane);
                }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    private ListView<Client> createMatchesList(DonatedOrgan organ) {
        // Setup the ListView
        final ListView<Client> matchesList = new ListView<>();

        List<Client> potentialMatches = State.getClientManager().getOrganMatches(organ);
        matchesList.setItems(FXCollections.observableArrayList(potentialMatches));

        matchesList.setCellFactory(param -> new PotentialRecipientCell(param.getItems()));

        matchesList.setOrientation(Orientation.HORIZONTAL);
        matchesList.setMinWidth(380);
        matchesList.setMaxHeight(250);
        matchesList.setFixedCellSize(190);

        matchesLists.add(matchesList);

        return matchesList;
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
    }

    private void layoutOrganNodes(double radius) {
        final int numNodes = organNodes.size();
        final double angleSize = (Math.PI * 2) / numNodes;

        Bounds bounds = deceasedDonorPane.getBoundsInParent();
        double centreX = bounds.getMinX() + bounds.getWidth() / 2;
        double centreY = bounds.getMinY() + bounds.getHeight() / 2;
        for (int i = 0; i < numNodes; i++) {
            setPositionUsingTransform(organNodes.get(i),
                    centreX + radius * Math.sin(angleSize * i),
                    centreY + radius * Math.cos(angleSize * i),
                    360 - Math.toDegrees(angleSize * i), 1);
        }
    }

    @FXML
    private void closeSpiderWeb() {
        // Close existing windows, but save them for later
        MultitouchHandler.setPhysicsHandler(new PhysicsHandler(MultitouchHandler.getRootPane()));

        // Close all windows for the spider web and clear
        for (MainController mainController : State.getMainControllers()) {
            mainController.closeWindow();
        }
        canvas.getChildren().clear();

        // Open all the previously open windows again
        for (MainController mainController : previouslyOpenWindows) {
            mainController.showWindow();
        }
    }

    private void updateMatchesListPosition(ListView<Client> matchesList, Transform newTransform, Bounds bounds) {
        matchesList.getTransforms().removeIf(transform -> transform instanceof Affine);

        Affine transform = new Affine();
        transform.prepend(new Scale(0.5, 0.5));
        transform.prepend(new Translate(-50, 120));
        transform.prepend(newTransform);

        matchesList.getTransforms().add(transform);

//        Affine transform = new Affine();
//        double x = deceasedDonorPane.getLocalToParentTransform().getTx() + radius * Math.sin(angleSize * i);
//        double y = deceasedDonorPane.getLocalToParentTransform().getTy() + radius * Math.cos(angleSize * i);
//        double angle = 360 - Math.toDegrees(angleSize * i);
//        transform.prepend(new Translate(x, y));
//        transform.prepend(new Rotate(angle, x, y));
//        matchesLists.get(i).getTransforms().add(transform);

    }
}
