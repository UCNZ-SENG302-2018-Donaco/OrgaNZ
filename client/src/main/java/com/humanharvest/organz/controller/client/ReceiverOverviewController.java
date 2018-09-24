package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

public class ReceiverOverviewController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ReceiverOverviewController.class.getName());

    private Client viewedClient;
    private Client donor;
    private Organ organ;
    private TransplantRequest viewedTransplantRequest;

    @FXML
    private ImageView imageView;

    @FXML
    private Label travelTime;

    @FXML
    private Label requestedTime;

    @FXML
    private Label name;

    @FXML
    private Label hospital;

    @FXML
    private Label priority;

    @FXML
    private VBox receiverVBox;

    /**
     * Initializes the UI for this page.
     */
    private void setClientFields() {

        // Set name, age, weight, and height
        name.setText(viewedClient.getPreferredNameFormatted());
        Double nameSize = Math.min(name.getFont().getSize(), 300 / name.getText().length());
        Font nameFont = Font.font(null, FontWeight.SEMI_BOLD, nameSize);
        name.setFont(nameFont);

        // Set hospital
        if (viewedClient.getHospital() == null) {
            hospital.setText("Unknown");
        } else {
            hospital.setText(viewedClient.getHospital().getName());
        }

        // Set travel time
        if (donor != null && viewedClient.getHospital() != null && donor.getHospital() != null) {
            Duration timeBetweenHospitals = viewedClient.getHospital().calculateTimeTo(donor.getHospital());
            if (timeBetweenHospitals.isZero()) {
                travelTime.setText("None");
            } else {
                travelTime.setText(DurationFormatter.getFormattedDuration(timeBetweenHospitals, DurationFormat.BIGGEST)
                        + String.format(Locale.UK, "%n(%.0f km)",
                        viewedClient.getHospital().calculateDistanceTo(donor.getHospital())));
            }
        } else {
            travelTime.setText("Unknown");
        }

        // Set wait time
        viewedClient.setTransplantRequests(State.getClientResolver().getTransplantRequests(viewedClient));
        viewedTransplantRequest = viewedClient.getTransplantRequest(organ);
        updateWaitTime();

        // Set image
        loadImage();

        // Track the adding of panes with the spiderweb pane collection.
        receiverVBox.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                MainController newMain = PageNavigator.openNewWindow();
                if (newMain != null) {
                    newMain.setWindowContext(new WindowContextBuilder()
                            .setAsClinicianViewClientWindow()
                            .viewClient(viewedClient)
                            .build());
                    PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                }
            }
        });

    }

    private void updateWaitTime() {
        if (viewedTransplantRequest == null) {
            requestedTime.setText("Error: no request");
        } else {
            Duration waitTime = viewedTransplantRequest.getTimeSinceRequest();
            requestedTime.setText(DurationFormatter.getFormattedDuration(waitTime, DurationFormat.BIGGEST));
        }
    }

    public void setup(Client client) {
        viewedClient = client;
        refresh();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (windowContext == null) {
            viewedClient = State.getSpiderwebDonor();
        } else {
            viewedClient = windowContext.getViewClient();
        }
        refresh();
    }

    @Override
    public void refresh() {
        setClientFields();
    }

    private void loadImage() {
        byte[] bytes;
        try {
            bytes = State.getImageManager().getClientImage(viewedClient.getUid());
        } catch (NotFoundException ignored) {
            try {
                bytes = State.getImageManager().getDefaultImage();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "IO Exception when loading image ", e);
                return;
            }
        } catch (ServerRestException e) {
            PageNavigator
                    .showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                            + "Please try again later.", mainController.getStage());
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        Image image = new Image(new ByteArrayInputStream(bytes));
        imageView.setImage(image);
    }

    @FXML
    private void initialize() {

        // Setup ticking
        final Timeline clock = new Timeline(new KeyFrame(
                javafx.util.Duration.millis(1000),
                event -> updateWaitTime()));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }

    public void setPriority(int priority) {
        this.priority.setText("#" + Integer.toString(priority));
    }
}