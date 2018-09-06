package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.NotFoundException;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.PageNavigator;

public class ReceiverOverviewController extends ViewBaseController {

    private static final Logger LOGGER = Logger.getLogger(ReceiverOverviewController.class.getName());

    private Client viewedClient;
    private TransplantRequest transplantRequest;

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
    private Label age;

    public ReceiverOverviewController(Client client, Organ organ) {
        viewedClient = client;
        transplantRequest = viewedClient.getTransplantRequest(organ);
    }

    public ReceiverOverviewController() {
        viewedClient = State.getClientManager().getClients().get(0);
        transplantRequest = viewedClient.getTransplantRequest(Organ.LIVER);
    }

    /**
     * Initializes the UI for this page.
     */
    @FXML
    private void initialize() {
        name.setText(viewedClient.getFullName());
        loadImage();
        age.setText(String.valueOf(viewedClient.getAge()));
        hospital.setText("X Hospital"); //todo
        travelTime.setText("x hours"); //todo
        if (transplantRequest == null) {
            requestedTime.setText("ERROR: not requested");
        } else {
            requestedTime.setText(getFormattedDuration(transplantRequest.getTimeSinceRequest()));
        }

    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        refresh();
    }

    @Override
    public void refresh() {
        //TODO
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
            PageNavigator.showAlert(AlertType.ERROR, "Server Error", "Something went wrong with the server. "
                    + "Please try again later.", mainController.getStage());
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return;
        }
        Image image = new Image(new ByteArrayInputStream(bytes));
        imageView.setImage(image);
    }

}
