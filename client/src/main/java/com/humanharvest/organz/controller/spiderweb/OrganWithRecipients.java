package com.humanharvest.organz.controller.spiderweb;

import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateConnectorText;
import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateDonorConnector;
import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateMatchesListPosition;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.client.ReceiverOverviewController;
import com.humanharvest.organz.controller.components.ExpiryBarUtils;
import com.humanharvest.organz.controller.components.PotentialRecipientCell;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.touch.FocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.OrganFocusArea;
import com.humanharvest.organz.touch.PointUtils;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;

public class OrganWithRecipients {

    private static final Logger LOGGER = Logger.getLogger(OrganWithRecipients.class.getName());

    private static final DurationFormat durationFormat = DurationFormat.X_HRS_Y_MINS_SECS;
    private static final int ORGAN_SIZE = 70;

    private final Pane deceasedDonorPane;
    private DonatedOrgan organ;
    private final Pane matchesPane;
    private final Pane canvas;
    private Timeline refresher;

    private OrganImageController organImageController;
    private Pane organPane;
    private List<PotentialRecipientCell> recipientCells = new ArrayList<>();
    private TransplantRecord transplantRecord;

    private Line deceasedToOrganConnector;
    private Line organToRecipientConnector;
    private Text durationText;

    public OrganWithRecipients(DonatedOrgan organ, Pane deceasedDonorPane,
            Pane canvas) {
        this.organ = organ;
        this.deceasedDonorPane = deceasedDonorPane;
        this.canvas = canvas;

        MainController newMain = ((PageNavigatorTouch) PageNavigator.getInstance())
                .openNewWindow(ORGAN_SIZE, ORGAN_SIZE, pane -> new OrganFocusArea(pane, this));
        newMain.getStyles().clear();

        createOrganImage(newMain);

        createLines();

        matchesPane = new Pane();
        MultitouchHandler.addPane(matchesPane);
        FocusArea matchesPaneFocus = (FocusArea) matchesPane.getUserData();
        matchesPaneFocus.setScalable(false);
        matchesPaneFocus.setRotatable(false);
        matchesPaneFocus.setTranslatable(false);

        enableHandlers();

        refresh();
    }

    public void refresh() {
        drawMatchesPane();
        createRefresher();
    }

    public void refresh(DonatedOrgan organ) {
        this.organ = organ;
        refresh();
    }

    private void createRefresher() {
        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
        updateConnectorText(durationText, organ, deceasedToOrganConnector);
        // Attach timer to update connector each second (for time until expiration)
        if (organ.getState() == OrganState.TRANSPLANT_COMPLETED) {
            if (refresher != null) {
                refresher.stop();
            }
            refresher = null;
        } else {
            refresher = new Timeline(new KeyFrame(
                    Duration.seconds(1),
                    event -> {
                        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
                        updateConnectorText(durationText, organ, deceasedToOrganConnector);
                    }));
            refresher.setCycleCount(Animation.INDEFINITE);
            refresher.play();
        }
    }

    private void createOrganImage(MainController newMain) {
        organImageController = (OrganImageController) PageNavigator
                .loadPage(Page.ORGAN_IMAGE, newMain);
        organImageController.loadImage(organ.getOrganType());

        organPane = newMain.getPane();
        FocusArea organFocus = (FocusArea) organPane.getUserData();

        organFocus.setScalable(false);
        organFocus.setCollidable(true);
        organFocus.setDisableHinting(true);
    }

    private void createLines() {
        // Create the lines
        deceasedToOrganConnector = new Line();
        deceasedToOrganConnector.setStrokeWidth(4);
        durationText = new Text(ExpiryBarUtils.getDurationString(organ, durationFormat));

        organToRecipientConnector = new Line();
        organToRecipientConnector.setStrokeWidth(4);

        canvas.getChildren().add(0, deceasedToOrganConnector);
        canvas.getChildren().add(0, organToRecipientConnector);
        canvas.getChildren().add(0, durationText);
    }

    private void drawMatchesPane() {
        organImageController.matchCountIsVisible(false);
        // Create match pane or matches list pane
        switch (organ.getState()) {
            case CURRENT:
            case NO_EXPIRY:
                List<TransplantRequest> potentialMatches = State.getClientManager().getMatchingOrganTransplants(organ);

                setMatchPane(createMatchesPane(FXCollections.observableArrayList(potentialMatches)));

                organImageController.setMatchCount(potentialMatches.size());

                if (potentialMatches.isEmpty() && !organ.hasExpired()) {
                    organImageController.matchCountIsVisible(true);
                }
                break;

            case TRANSPLANT_COMPLETED:
            case TRANSPLANT_PLANNED:
                transplantRecord = State.getClientManager().getMatchingOrganTransplantRecord(organ);

                setMatchPane(createMatchPane(transplantRecord));
                break;

            default:
                removeMatchPane();
        }

        updateRecipientConnector();
    }

    private void removeMatchPane() {
        matchesPane.getChildren().clear();
    }

    private void setMatchPane(Pane pane) {
        removeMatchPane();
        matchesPane.getChildren().add(pane);
    }

    public Pane getOrganPane() {
        return organPane;
    }

    public DonatedOrgan getOrgan() {
        return organ;
    }

    private void enableHandlers() {
        // Redraws lines when organs or donor pane is moved

        organPane.localToParentTransformProperty().addListener(
                (__, ___, newValue) -> handleOrganPaneTransformed(newValue));

        matchesPane.localToParentTransformProperty().addListener(
                (__, ___, ____) -> handlePotentialMatchesTransformed());

        organPane.setOnMouseClicked(this::handleOrganPaneClick);

        // This listener is only used to handle the initial reposition
        matchesPane.boundsInLocalProperty().addListener(
                (__, ___, newValue) -> handleOrganPaneTransformed(organPane.getLocalToParentTransform()));
    }

    public void handleOrganPaneClick(MouseEvent event) {
        if (event.isSynthesized()) {
            return;
        }

        if (event.getClickCount() == 1) {
            handleOrganSingleClick();
        } else if (event.getClickCount() == 2 && !organ.hasExpiredNaturally()) {
            handleOrganDoubleClick();
        }
    }

    public void handleOrganSingleClick() {
        if (organ.getState() == OrganState.CURRENT || organ.getState() == OrganState.NO_EXPIRY) {
            matchesPane.setVisible(!matchesPane.isVisible());
            organImageController.matchCountIsVisible(!matchesPane.isVisible());
        }
    }

    public void handleOrganDoubleClick() {
        if (organ.getState() == OrganState.CURRENT || organ.getState() == OrganState.NO_EXPIRY) {
            String reason = "Manually Overridden by Doctor using WebView";
            State.getClientResolver().manuallyOverrideOrgan(organ, reason);
            organ.manuallyOverride(reason);

            removeMatchPane();

        } else if (organ.getState() == OrganState.OVERRIDDEN) {
            State.getClientResolver().cancelManualOverrideForOrgan(organ);
            organ.cancelManualOverride();

            drawMatchesPane();
        }

        organImageController.matchCountIsVisible(false);
        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
        updateConnectorText(durationText, organ, deceasedToOrganConnector);
    }

    public void handleOrganPaneTouchReleased() {
        System.out.println("Released");
        switch (organ.getState()) {
            case CURRENT:
            case NO_EXPIRY:
                tryScheduleTransplant();
                break;
            case TRANSPLANT_PLANNED:
            case TRANSPLANT_COMPLETED:
                tryCancelTransplant();
                break;
            default:
                break;
        }
    }

    private void tryCancelTransplant() {
        if (PointUtils.distance(PointUtils.getCentreOfNode(organPane), PointUtils.getCentreOfNode(matchesPane)) > 200) {
            try {
                State.getClientResolver().deleteProcedureRecord(transplantRecord.getClient(), transplantRecord);
                Optional<DonatedOrgan> optionalOrgan = State.getClientResolver().getDonatedOrgans(organ.getDonor())
                        .stream()
                        .filter(newOrgan -> newOrgan.getId().equals(organ.getId()))
                        .findFirst();
                if (optionalOrgan.isPresent()) {
                    this.organ = optionalOrgan.get();
                    refresh();
                } else {
                    throw new NotFoundException();
                }
            } catch (ServerRestException | NotFoundException exc) {
                Notifications.create()
                        .title("Server Error")
                        .text("An error occurred when trying to remove the transplant.")
                        .showError();
            }
        }
    }

    private void tryScheduleTransplant() {
        Optional<PotentialRecipientCell> closestCell = recipientCells.stream()
                .filter(cell -> organIntersectsCell(organPane, cell))
                .filter(cell -> !cell.isEmpty())
                .min(Comparator.comparing(cell -> PointUtils.distance(PointUtils.getCentreOfNode(cell),
                        PointUtils.getCentreOfNode(organPane))));

        if (closestCell.isPresent()) {
            TransplantRequest request = closestCell.get().getTransplantRequest();
            scheduleTransplant(organ, request);
        }
    }

    private void scheduleTransplant(DonatedOrgan organ, TransplantRequest request) {
        Set<Hospital> hospitals = State.getConfigManager().getHospitals();

        Hospital nearestHospital;
        if (request.getClient().getHospital() != null) {
            // Recipient has a hospital
            nearestHospital = request.getClient().getHospital()
                    .getNearestWithTransplantProgram(organ.getOrganType(), hospitals);
        } else {
            try {
                // Recipient has no hospital, but has a region
                final Region region = Region.fromString(request.getClient().getRegion());
                nearestHospital = hospitals.stream()
                        .filter(hospital -> hospital.getTransplantPrograms().contains(organ.getOrganType()))
                        .min(Comparator.comparing(hospital -> hospital.calculateDistanceTo(region)))
                        .orElse(null);
            } catch (IllegalArgumentException exc) {
                // Neither hospital nor region, so get nearest to the organ's donor
                Hospital organHospital = organ.getDonor().getHospital();
                if (organHospital == null) {
                    nearestHospital = hospitals.stream()
                            .filter(hospital -> hospital.getTransplantPrograms().contains(organ.getOrganType()))
                            .findAny().orElse(null);
                } else {
                    nearestHospital = organ.getDonor().getHospital()
                            .getNearestWithTransplantProgram(organ.getOrganType(), hospitals);
                }
            }
        }

        if (nearestHospital == null) {
            Notifications.create()
                    .title("No Valid Hospital")
                    .text(String.format("There is no hospital that can transplant %s.",
                            organ.getOrganType().toString()))
                    .showError();
        } else {
            final LocalDate transplantDate = LocalDateTime.now()
                    .plus(nearestHospital.calculateTimeTo(organ.getDonor().getHospital()))
                    .toLocalDate();
            //TODO: This fails if the Donor has no hospital
            try {
                State.getClientResolver().scheduleTransplantProcedure(organ, request, nearestHospital, transplantDate);
                this.organ = State.getClientManager().getMatchingOrganTransplantRecord(organ).getOrgan();
                refresh();
            } catch (ServerRestException exc) {
                Notifications.create()
                        .title("Server Error")
                        .text("An error occurred when trying to schedule the transplant.")
                        .showError();
            }
        }
    }

    private boolean organIntersectsCell(Node organPane, Node recipientCell) {
        Bounds cellBounds = recipientCell.localToScene(recipientCell.getBoundsInLocal());
        cellBounds = new BoundingBox(
                cellBounds.getMinX() + cellBounds.getWidth() / 4,
                cellBounds.getMinY() + cellBounds.getHeight() / 4,
                cellBounds.getWidth() / 2,
                cellBounds.getHeight() / 2);
        return organPane.localToScene(organPane.getBoundsInLocal()).intersects(cellBounds);
    }

    public void handleOrganPaneTransformed(Transform newValue) {
        Bounds bounds = organPane.getBoundsInParent();
        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
        setDonorConnectorEnd(bounds);
        updateConnectorText(durationText, organ, deceasedToOrganConnector);

        updateMatchesListPosition(matchesPane, newValue, ORGAN_SIZE);

        for (Node cell : recipientCells) {
            if (organIntersectsCell(organPane, cell)) {
                DropShadow dropShadow = new DropShadow(15, Color.PALEGOLDENROD);
                dropShadow.setInput(new Glow(0.5));
                cell.setEffect(dropShadow);
            } else {
                cell.setEffect(null);
            }
        }

        setRecipientConnectorStart(bounds);
        setRecipientConnectorEnd(matchesPane.getBoundsInParent());

        organPane.toFront();
    }

    public void handlePotentialMatchesTransformed() {
        setRecipientConnectorEnd(matchesPane.getBoundsInParent());
    }

    public void updateRecipientConnector() {
        switch (organ.getState()) {
            case TRANSPLANT_PLANNED:
                organToRecipientConnector.setStroke(Color.PURPLE);
                organToRecipientConnector.setVisible(true);
                break;
            case TRANSPLANT_COMPLETED:
                organToRecipientConnector.setStroke(Color.BLUE);
                organToRecipientConnector.setVisible(true);
                break;
            default:
                organToRecipientConnector.setVisible(false);
        }
    }

    private void setRecipientConnectorStart(Bounds bounds) {
        organToRecipientConnector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
        organToRecipientConnector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private void setRecipientConnectorEnd(Bounds bounds) {
        organToRecipientConnector.setEndX(bounds.getMinX() + bounds.getWidth() / 2);
        organToRecipientConnector.setEndY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    public void setDonorConnectorStart(Bounds bounds) {
        deceasedToOrganConnector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
        deceasedToOrganConnector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private void setDonorConnectorEnd(Bounds bounds) {
        deceasedToOrganConnector.setEndX(bounds.getMinX() + bounds.getWidth() / 2);
        deceasedToOrganConnector.setEndY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private Pane createMatchPane(TransplantRecord record) {
        Pane pane = new Pane();
        pane.getStylesheets().add(getClass().getResource("/css/matched-recipient.css").toExternalForm());

        if (record != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        PotentialRecipientCell.class.getResource(Page.RECEIVER_OVERVIEW.getPath()));
                Node node = loader.load();
                ReceiverOverviewController controller = loader.getController();
                controller.setup(record, organ.getDonor());
                if (organ.getState() == OrganState.TRANSPLANT_PLANNED) {
                    controller.setPriority("Scheduled");
                } else if (organ.getState() == OrganState.TRANSPLANT_COMPLETED) {
                    controller.setPriority("Completed");
                } else {
                    controller.setPriority(-1);
                }
                pane.getChildren().add(node);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        return pane;
    }

    private Pane createMatchesPane(ObservableList<TransplantRequest> potentialMatches) {
        // Setup the ListView
        final ListView<TransplantRequest> matchesList = new ListView<>();
        matchesList.getStylesheets().add(getClass().getResource("/css/list-view-cell-gap.css").toExternalForm());

        matchesList.setItems(potentialMatches);

        matchesList.setCellFactory(param -> {
            PotentialRecipientCell cell = new PotentialRecipientCell(param.getItems(), organ.getDonor());
            recipientCells.add(cell);
            return cell;
        });

        matchesList.setOrientation(Orientation.HORIZONTAL);
        matchesList.setMinWidth(380);
        matchesList.setMaxHeight(250);
        matchesList.setFixedCellSize(190);

        return new Pane(matchesList);
    }
}
