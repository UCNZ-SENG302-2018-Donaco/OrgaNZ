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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.DurationFormat;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

public class ReceiverOverviewController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ReceiverOverviewController.class.getName());

    private Client recipient;
    private Client donor;
    private TransplantRequest request;

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
        name.setText(recipient.getPreferredNameFormatted());
        Double nameSize = Math.min(name.getFont().getSize(), 300.0 / name.getText().length());
        Font nameFont = Font.font(null, FontWeight.SEMI_BOLD, nameSize);
        name.setFont(nameFont);

        // Set hospital
        if (recipient.getHospital() == null) {
            hospital.setText("Unknown");
        } else {
            hospital.setText(recipient.getHospital().getName());
        }

        // Set travel time
        if (donor != null && recipient.getHospital() != null && donor.getHospital() != null) {
            Duration timeBetweenHospitals = recipient.getHospital().calculateTimeTo(donor.getHospital());
            if (timeBetweenHospitals.isZero()) {
                travelTime.setText("None");
            } else {
                travelTime.setText(DurationFormatter.getFormattedDuration(timeBetweenHospitals, DurationFormat.BIGGEST)
                        + String.format(Locale.UK, "%n(%.0f km)",
                        recipient.getHospital().calculateDistanceTo(donor.getHospital())));
            }
        } else {
            travelTime.setText("Unknown");
        }

        // Set wait time
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
                            .viewClient(recipient)
                            .build());
                    PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                }
            }
        });

    }

    private void updateWaitTime() {
        if (request == null) {
            requestedTime.setText("Error: no request");
        } else {
            Duration waitTime = request.getTimeSinceRequest();
            requestedTime.setText(DurationFormatter.getFormattedDuration(waitTime, DurationFormat.BIGGEST));
        }
    }

    public void setup(TransplantRequest request, Client donor) {
        this.request = request;
        this.recipient = request.getClient();
        this.donor = donor;
        refresh();
    }

    public void setup(TransplantRecord record, Client donor) {
        this.request = record.getRequest();
        this.recipient = record.getReceiver();
        this.donor = donor;
        refresh();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (windowContext == null) {
            recipient = State.getSpiderwebDonor();
        } else {
            recipient = windowContext.getViewClient();
        }
        refresh();
    }

    @Override
    public void refresh() {
        setClientFields();
    }

    private void loadImage() {
        Task<byte[]> task = new Task<byte[]>() {
            @Override
            protected byte[] call() throws ServerRestException, IOException {
                try {
                    return com.humanharvest.organz.state.State.getImageManager().getClientImage(recipient.getUid());
                } catch (NotFoundException exc) {
                    return com.humanharvest.organz.state.State.getImageManager().getDefaultImage();
                }
            }
        };

        task.setOnSucceeded(event -> {
            Image image = new Image(new ByteArrayInputStream(task.getValue()));
            imageView.setImage(image);
        });

        task.setOnFailed(event -> {
            try {
                throw task.getException();
            } catch (IOException exc) {
                LOGGER.log(Level.SEVERE, "IOException when loading default image.", exc);
            } catch (ServerRestException exc) {
                LOGGER.log(Level.SEVERE, "", exc);
                Notifications.create()
                        .title("Server Error")
                        .text("A client's profile picture could not be retrieved from the server.")
                        .showError();
            } catch (Throwable exc) {
                LOGGER.log(Level.SEVERE, exc.getMessage(), exc);
            }
        });

        new Thread(task).start();
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
        if (priority == -1) {
            this.priority.setVisible(false);
        } else {
            this.priority.setVisible(true);
            this.priority.setText("#" + Integer.toString(priority));
        }
    }

    public void setPriority(String text) {
        priority.setVisible(true);
        priority.setText(text);
    }

    public TransplantRequest getRequest() {
        return request;
    }
}