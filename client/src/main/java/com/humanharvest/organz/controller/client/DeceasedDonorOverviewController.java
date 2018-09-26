package com.humanharvest.organz.controller.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
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

        displayData();
        loadImage();
    }

    @Override
    public void refresh() {
        Optional<Client> optionalClient = State.getClientManager().getClientByID(deceasedDonor.getUid());
        if (!optionalClient.isPresent()) {
            Notifications.create()
                    .title("Server Error")
                    .text("Could not refresh the information for the donor.")
                    .showError();
        } else {
            deceasedDonor = optionalClient.get();
            displayData();
            loadImage();
        }
    }

    private void displayData() {
        nameLabel.setText(deceasedDonor.getFullName());
        timeOfDeathLabel.setText(formatTimeOfDeath(deceasedDonor.getDatetimeOfDeath()));
        hospitalLabel.setText(deceasedDonor.getHospital() == null ? "Location Unknown" :
                deceasedDonor.getHospital().getName());
        if (deceasedDonor.getDonatedOrgans() == null) {
            numOrgansLabel.setText("Number of donated organs unknown.");
        } else {
            long numAvailableOrgans = deceasedDonor.getDonatedOrgans().stream()
                    .filter(DonatedOrgan::isAvailable)
                    .count();
            numOrgansLabel.setText("Number of available organs: " + numAvailableOrgans);
        }
    }

    private void loadImage() {
        byte[] bytes;
        try {
            bytes = State.getImageManager().getClientImage(deceasedDonor.getUid());
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
}
