package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.controlsfx.control.Notifications;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.SubController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigator;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;

public class DeceasedDonorOverviewController extends SubController {

    private static final Logger LOGGER = Logger.getLogger(DeceasedDonorOverviewController.class.getName());
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private Pane deceasedDonorPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Label nameLabel;
    @FXML
    private Label timeOfDeathLabel;
    @FXML
    private Label hospitalLabel;
    @FXML
    private Label numOrgansLabel;

    private Client deceasedDonor;

    private static String formatTimeOfDeath(LocalDateTime dateTimeOfDeath) {
        if (dateTimeOfDeath.toLocalDate().equals(LocalDate.now().minusDays(1))) {
            // Yesterday
            return String.format("Died at %s yesterday.",
                    dateTimeOfDeath.toLocalTime().format(TIME_FORMAT));
        } else if (dateTimeOfDeath.toLocalDate().equals(LocalDate.now())) {
            // Today
            return String.format("Died at %s today.",
                    dateTimeOfDeath.toLocalTime().format(TIME_FORMAT));
        } else if (dateTimeOfDeath.getYear() == LocalDate.now().getYear()) {
            // This year
            return String.format("Died at %s, %s.", dateTimeOfDeath.toLocalTime().format(TIME_FORMAT),
                    dateTimeOfDeath.toLocalDate().format(DateTimeFormatter.ofPattern("d MMM")));
        } else {
            // Another year
            return String.format("Died at %s, %s.", dateTimeOfDeath.toLocalTime().format(TIME_FORMAT),
                    dateTimeOfDeath.toLocalDate().format(DateTimeFormatter.ofPattern("d/M/y")));
        }
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        if (windowContext == null) {
            deceasedDonor = State.getSpiderwebDonor();
        } else {
            deceasedDonor = windowContext.getViewClient();
        }

        // Setup handling of double-click
        deceasedDonorPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                MainController newMain = PageNavigator.openNewWindow();
                if (newMain != null) {
                    newMain.setWindowContext(new WindowContextBuilder()
                            .setAsClinicianViewClientWindow()
                            .viewClient(deceasedDonor)
                            .build());
                    PageNavigator.loadPage(Page.VIEW_CLIENT, newMain);
                }
            }
        });

        updateClientDetails();
        updateOrganCount(deceasedDonor.getDonatedOrgans());
        updateImage();
    }

    /**
     * Refreshes the Donors information and image
     */
    @Override
    public void refresh() {
        fetchClient();
        fetchOrgans();
        updateImage();
    }

    private void fetchClient() {
        Task<Optional<Client>> clientTask = new Task<Optional<Client>>() {
            @Override
            protected Optional<Client> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getClientManager().getClientByID(deceasedDonor.getUid());
            }
        };

        clientTask.setOnSucceeded(event -> {
            Optional<Client> optionalClient = clientTask.getValue();
            if (optionalClient.isPresent()) {
                deceasedDonor = optionalClient.get();
                updateClientDetails();
            } else {
                handleError();
            }
        });

        clientTask.setOnFailed(err -> handleError());

        new Thread(clientTask).start();
    }

    private void updateClientDetails() {
        nameLabel.setText(deceasedDonor.getFullName());

        timeOfDeathLabel.setText(formatTimeOfDeath(deceasedDonor.getDatetimeOfDeath()));

        if (deceasedDonor.getHospital() == null) {
            hospitalLabel.setText("Location Unknown");
        } else {
            hospitalLabel.setText(deceasedDonor.getHospital().getName());
        }
    }

    public void fetchOrgans() {
        Task<Collection<DonatedOrgan>> organsTask = new Task<Collection<DonatedOrgan>>() {
            @Override
            protected Collection<DonatedOrgan> call() throws ServerRestException {
                return com.humanharvest.organz.state.State.getClientResolver().getDonatedOrgans(deceasedDonor);
            }
        };

        organsTask.setOnSucceeded(event -> updateOrganCount(organsTask.getValue()));

        organsTask.setOnFailed(err -> handleError());

        new Thread(organsTask).start();
    }

    private void updateOrganCount(Collection<DonatedOrgan> donatedOrgans) {
        if (donatedOrgans == null) {
            numOrgansLabel.setText("Number of donated organs unknown.");
        } else {
            long numAvailableOrgans = donatedOrgans.stream()
                    .filter(DonatedOrgan::isAvailable)
                    .count();
            numOrgansLabel.setText("Number of available organs: " + numAvailableOrgans);
        }
    }

    private void handleError() {
        Notifications.create()
                .title("Server Error")
                .text("Could not refresh the information for the donor.")
                .showError();
    }

    private void updateImage() {
        Task<byte[]> task = new Task<byte[]>() {
            @Override
            protected byte[] call() throws ServerRestException, IOException {
                try {
                    return com.humanharvest.organz.state.State.getImageManager().getClientImage(deceasedDonor.getUid());
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
}
