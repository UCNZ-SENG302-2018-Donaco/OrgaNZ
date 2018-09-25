package com.humanharvest.organz.controller.spiderweb;

import static com.humanharvest.organz.controller.spiderweb.LineFormatters.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.DonatedOrgan.OrganState;
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
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.PageNavigatorTouch;

public class OrganWithRecipients {

    private static final DurationFormat durationFormat = DurationFormat.X_HRS_Y_MINS_SECS;

    private Pane deceasedDonorPane;

    private DonatedOrgan organ;
    private List<TransplantRequest> potentialMatches;

    private OrganImageController organImageController;
    private Pane organPane;
    private Pane matchesPane;
    private List<PotentialRecipientCell> recipientCells = new ArrayList<>();

    private Line deceasedToOrganConnector;
    private Line organToRecipientConnector;
    private Text durationText;

    public OrganWithRecipients(DonatedOrgan organ, List<TransplantRequest> potentialMatches, Pane deceasedDonorPane,
            Pane canvas) {
        this.organ = organ;
        this.potentialMatches = potentialMatches;
        this.deceasedDonorPane = deceasedDonorPane;

        MainController newMain = ((PageNavigatorTouch) PageNavigator.getInstance())
                .openNewWindow(70, 70, pane -> new OrganFocusArea(pane, this));
        newMain.getStyles().clear();

        createOrganImage(newMain);

        createMatchesList();

        // Create the lines
        deceasedToOrganConnector = new Line();
        deceasedToOrganConnector.setStrokeWidth(4);
        durationText = new Text(ExpiryBarUtils.getDurationString(organ, durationFormat));

        organToRecipientConnector = new Line();
        organToRecipientConnector.setStrokeWidth(4);

        matchesPane.visibleProperty().addListener((observable, oldValue, newValue) -> {
            organToRecipientConnector.setVisible(newValue);
        });

        canvas.getChildren().add(0, deceasedToOrganConnector);
        canvas.getChildren().add(0, organToRecipientConnector);
        canvas.getChildren().add(0, durationText);

        enableHandlers();

        setDonorConnectorStart(deceasedDonorPane.getBoundsInParent());
        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
        updateConnectorText(durationText, organ, deceasedToOrganConnector);

        // Attach timer to update connector each second (for time until expiration)
        Timeline clock = new Timeline(new KeyFrame(
                Duration.seconds(1),
                event -> {
                    updateDonorConnector(organ, deceasedToOrganConnector, organPane);
                    updateRecipientConnector(organ, organToRecipientConnector);
                    updateConnectorText(durationText, organ, deceasedToOrganConnector);
                }));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void createOrganImage(MainController newMain) {
        organImageController = (OrganImageController) PageNavigator
                .loadPage(Page.ORGAN_IMAGE, newMain);
        organImageController.loadImage(organ.getOrganType());
        organImageController.setMatchCount(potentialMatches.size());

        if (potentialMatches.isEmpty() && !organ.hasExpired()) {
            organImageController.matchCountIsVisible(true);
        }

        organPane = newMain.getPane();
        FocusArea organFocus = (FocusArea) organPane.getUserData();

        organFocus.setScalable(false);
        organFocus.setCollidable(true);
        organFocus.setDisableHinting(true);
    }

    private void createMatchesList() {
        // Double click to override and organ or to unoverride
        // Create matches list
//        if (organ.getState() == OrganState.CURRENT || organ.getState() == OrganState.NO_EXPIRY) {
        ListView<TransplantRequest> matchesListView = createMatchesList(
                FXCollections.observableArrayList(potentialMatches));
        matchesPane = new Pane(matchesListView);
        MultitouchHandler.addPane(matchesPane);
//        } else if (organ.getState() == OrganState.TRANSPLANT_COMPLETED ||
//                organ.getState() == OrganState.TRANSPLANT_PLANNED) {
//
//
//
//        }

    }

    public Pane getOrganPane() {
        return organPane;
    }

    private void enableHandlers() {
        // Redraws lines when organs or donor pane is moved
        deceasedDonorPane.localToParentTransformProperty().addListener(handleDeceasedDonorTransformed());
        organPane.localToParentTransformProperty().addListener(handleOrganPaneTransformed());
        matchesPane.localToParentTransformProperty().addListener(handlePotentialMatchesTransformed());

        organPane.setOnMouseClicked(this::handleOrganPaneClick);
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
        if (organ.getState() == OrganState.CURRENT) {
            matchesPane.setVisible(!matchesPane.isVisible());
            organImageController.matchCountIsVisible(!matchesPane.isVisible());
        }
    }

    public void handleOrganDoubleClick() {
        if (organ.getOverrideReason() == null) {
            String reason = "Manually Overridden by Doctor using WebView";
            State.getClientResolver().manuallyOverrideOrgan(organ, reason);
            organ.manuallyOverride(reason);

            matchesPane.setVisible(false);

        } else {
            State.getClientResolver().cancelManualOverrideForOrgan(organ);
            organ.cancelManualOverride();

            matchesPane.setVisible(true);
        }

        organImageController.matchCountIsVisible(false);
        updateDonorConnector(organ, deceasedToOrganConnector, organPane);
        updateConnectorText(durationText, organ, deceasedToOrganConnector);
    }

    public ChangeListener<Transform> handleDeceasedDonorTransformed() {
        return (observable, oldValue, newValue) -> {
            setDonorConnectorStart(deceasedDonorPane.getBoundsInParent());
            updateDonorConnector(organ, deceasedToOrganConnector, organPane);
            updateConnectorText(durationText, organ, deceasedToOrganConnector);
        };
    }

    public void handleOrganPaneTouchReleased() {
        Optional<PotentialRecipientCell> closestCell = recipientCells.stream()
                .filter(cell -> organIntersectsCell(organPane, cell))
                .min(Comparator.comparing(cell -> PointUtils.distance(PointUtils.getCentreOfNode(cell),
                        PointUtils.getCentreOfNode(organPane))));

        if (closestCell.isPresent()) {
            ReceiverOverviewController recipientOverview = closestCell.get().getRecipientOverview();
//            System.out.println(recipientOverview.getRecipient().getFullName());
            System.out.println("Do something");
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

    public ChangeListener<Transform> handleOrganPaneTransformed() {
        return (observable, oldValue, newValue) -> {
            Bounds bounds = organPane.getBoundsInParent();
            updateDonorConnector(organ, deceasedToOrganConnector, organPane);
            setDonorConnectorEnd(bounds);
            updateConnectorText(durationText, organ, deceasedToOrganConnector);

            updateMatchesListPosition(matchesPane, newValue);

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
            updateRecipientConnector(organ, organToRecipientConnector);

            organPane.toFront();
        };
    }

    public ChangeListener<Transform> handlePotentialMatchesTransformed() {
        return (observable, oldValue, newValue) -> {
            setRecipientConnectorEnd(matchesPane.getBoundsInParent());
            updateRecipientConnector(organ, organToRecipientConnector);
        };
    }

    private void setRecipientConnectorStart(Bounds bounds) {
        organToRecipientConnector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
        organToRecipientConnector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private void setRecipientConnectorEnd(Bounds bounds) {
        organToRecipientConnector.setEndX(bounds.getMinX() + bounds.getWidth() / 2);
        organToRecipientConnector.setEndY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private void setDonorConnectorStart(Bounds bounds) {
        deceasedToOrganConnector.setStartX(bounds.getMinX() + bounds.getWidth() / 2);
        deceasedToOrganConnector.setStartY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private void setDonorConnectorEnd(Bounds bounds) {
        deceasedToOrganConnector.setEndX(bounds.getMinX() + bounds.getWidth() / 2);
        deceasedToOrganConnector.setEndY(bounds.getMinY() + bounds.getHeight() / 2);
    }

    private ListView<TransplantRequest> createMatchesList(ObservableList<TransplantRequest> potentialMatches) {
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

        return matchesList;
    }
}
