package com.humanharvest.organz.controller.spiderweb;

import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateConnectorText;
import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateDonorConnector;
import static com.humanharvest.organz.controller.spiderweb.LineFormatters.updateMatchesListPosition;

import java.io.IOException;
import java.time.Duration;
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
import javafx.concurrent.Task;
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
import com.humanharvest.organz.touch.MatchesFocusArea;
import com.humanharvest.organz.touch.MultitouchHandler;
import com.humanharvest.organz.touch.OrganFocusArea;
import com.humanharvest.organz.touch.PointUtils;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;

public class OrganWithRecipients {

    private static final Logger LOGGER = Logger.getLogger(OrganWithRecipients.class.getName());

    private static final DurationFormat durationFormat = DurationFormat.X_HRS_Y_MINS_SECS;
    private static final int ORGAN_SIZE = 70;

    private final DropShadow hoveredGlow;

    private final Pane deceasedDonorPane;
    private final Pane matchesPane;
    private final Pane canvas;
    private DonatedOrgan organ;
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

        // Create the hovered glow effect for creating transplants
        hoveredGlow = new DropShadow(15, Color.PALEGOLDENROD);
        hoveredGlow.setInput(new Glow(0.5));

        MainController newMain = ((PageNavigatorTouch) PageNavigator.getInstance())
                .openNewWindow(ORGAN_SIZE, ORGAN_SIZE, pane -> new OrganFocusArea(pane, this));
        newMain.getStyles().clear();

        createOrganImage(newMain);

        createLines();

        matchesPane = new Pane();
        MatchesFocusArea matchesFocusArea = new MatchesFocusArea(matchesPane, this);
        MultitouchHandler.addPane(matchesPane, matchesFocusArea);
        matchesFocusArea.setScalable(false);
        matchesFocusArea.setRotatable(false);
        matchesFocusArea.setTranslatable(false);

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

        closeRefresher();
        if (organ.getState() != OrganState.TRANSPLANT_COMPLETED) {
            refresher = new Timeline(new KeyFrame(
                    javafx.util.Duration.seconds(1),
                    event -> {
                        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
                        updateConnectorText(durationText, organ, deceasedToOrganConnector);
                    }));
            refresher.setCycleCount(Animation.INDEFINITE);
            refresher.play();
        }
    }

    public void closeRefresher() {
        if (refresher != null) {
            refresher.stop();
            refresher = null;
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
                Task<List<TransplantRequest>> matchesTask = new Task<List<TransplantRequest>>() {
                    @Override
                    protected List<TransplantRequest> call() throws ServerRestException {
                        return com.humanharvest.organz.state.State.getClientManager()
                                .getMatchingOrganTransplants(organ);
                    }
                };

                matchesTask.setOnSucceeded(event -> {
                    List<TransplantRequest> potentialMatches = matchesTask.getValue();
                    setMatchPane(createMatchesPane(FXCollections.observableArrayList(potentialMatches)));

                    organImageController.setMatchCount(potentialMatches.size());
                    matchesPane.setVisible(false);
                    organImageController.matchCountIsVisible(true);
                    updateRecipientConnector();
                });

                new Thread(matchesTask).start();
                break;

            case TRANSPLANT_COMPLETED:
            case TRANSPLANT_PLANNED:
                Task<TransplantRecord> transplantTask = new Task<TransplantRecord>() {
                    @Override
                    protected TransplantRecord call() throws ServerRestException {
                        return com.humanharvest.organz.state.State.getClientManager()
                                .getMatchingOrganTransplantRecord(organ);
                    }
                };

                transplantTask.setOnSucceeded(event -> {
                    transplantRecord = transplantTask.getValue();
                    setMatchPane(createMatchPane(transplantRecord));
                    updateRecipientConnector();
                });

                new Thread(transplantTask).start();
                break;

            default:
                removeMatchPane();
                updateRecipientConnector();
        }
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
                (__, ___, ____) -> handleOrganPaneTransformed(organPane.getLocalToParentTransform()));
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

    public void handleTouchReleased() {
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
        handleOrganPaneTransformed(organPane.getLocalToParentTransform());
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
        Optional<PotentialRecipientCell> closestCell = getNearestCell();

        if (closestCell.isPresent()) {
            TransplantRequest request = closestCell.get().getTransplantRequest();
            scheduleTransplant(organ, request);
        }
    }

    private Optional<PotentialRecipientCell> getNearestCell() {
        return recipientCells.stream()
                .filter(cell -> organIntersectsCell(organPane, cell))
                .filter(cell -> !cell.isEmpty())
                .min(Comparator.comparing(cell -> PointUtils.distance(PointUtils.getCentreOfNode(cell),
                        PointUtils.getCentreOfNode(organPane))));
    }

    private void scheduleTransplant(DonatedOrgan organ, TransplantRequest request) {
        Set<Hospital> hospitals = State.getConfigManager().getHospitals();

        Hospital donorHospital = organ.getDonor().getHospital();
        if (donorHospital == null) {
            Notifications.create()
                    .title("No Donor Hospital")
                    .text("The Donor must have a hospital specified. The transplant has not been scheduled")
                    .showError();
            return;
        }

        Hospital recipientHospital = Hospital.getHospitalForClient(request.getClient(), hospitals);

        Hospital transplantHospital;
        Duration travelTime;
        if (recipientHospital != null) {
            transplantHospital = recipientHospital.getNearestWithTransplantProgram(organ.getOrganType(), hospitals);
            travelTime = recipientHospital.calculateTimeTo(transplantHospital);
        } else {
            transplantHospital = donorHospital.getNearestWithTransplantProgram(organ.getOrganType(), hospitals);
            travelTime = donorHospital.calculateTimeTo(transplantHospital);
        }
        LocalDate transplantDate = LocalDateTime.now().plus(travelTime).toLocalDate();

        try {
            State.getClientResolver().scheduleTransplantProcedure(organ, request, transplantHospital, transplantDate);
            this.organ = State.getClientManager().getMatchingOrganTransplantRecord(organ).getOrgan();
            refresh();
        } catch (ServerRestException exc) {
            Notifications.create()
                    .title("Server Error")
                    .text("An error occurred when trying to schedule the transplant.")
                    .showError();
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

        // Remove the cell effects if any were previously applied
        for (Node cell : recipientCells) {
            cell.setEffect(null);
        }
        // Set the hovered glow effect on the nearest hovered cell if it exists
        getNearestCell().ifPresent(cell -> cell.setEffect(hoveredGlow));

        setRecipientConnectorStart(bounds);
        setRecipientConnectorEnd(matchesPane.getBoundsInParent());
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
                controller.setup(record, organ.getDonor(), refresher);
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
            PotentialRecipientCell cell = new PotentialRecipientCell(param.getItems(), organ.getDonor(), refresher);
            recipientCells.add(cell);
            return cell;
        });

        matchesList.setOrientation(Orientation.HORIZONTAL);
        switch (potentialMatches.size()) {
            case 0:
                matchesList.setMinWidth(0);
                matchesList.setPrefWidth(0);
                matchesList.setMaxWidth(0);
            case 1:
                matchesList.setMaxWidth(176);
                break;
            case 2:
                matchesList.setMaxWidth(380);
                matchesList.setMinWidth(380);
                break;
            default:
                matchesList.setMinWidth(450);
                break;
        }

        matchesList.setMaxHeight(265);
        matchesList.setFixedCellSize(200);

        return new Pane(matchesList);
    }
}
