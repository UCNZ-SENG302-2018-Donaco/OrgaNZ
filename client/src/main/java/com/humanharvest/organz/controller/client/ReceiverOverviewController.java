package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.state.State.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.controller.clinician.ViewBaseController;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import com.humanharvest.organz.utilities.DurationFormatter;
import com.humanharvest.organz.utilities.DurationFormatter.Format;
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
    private final Session session;
    private final ClientManager manager;

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

    @FXML
    private VBox receiverVBox;

    /*
        public ReceiverOverviewController(Client client, Organ organ) {
            viewedClient = client;
            System.out.println(viewedClient.getFullName());
            System.out.println(viewedClient.getTransplantRequests().size());
            viewedTransplantRequest = viewedClient.getTransplantRequest(organ);
        }
    */
    public ReceiverOverviewController() { // test with first client
        manager = getClientManager();
        session = getSession();
    }

    /**
     * Initializes the UI for this page.
     */
    private void setClientFields() {
        //todo replace dummy donor and organ
        Client dummyDonor = new Client();
        dummyDonor.setHospital(viewedClient.getHospital());
        donor = dummyDonor;
        organ = Organ.LIVER;

        // Set name and age
        name.setText(viewedClient.getFullName());
        age.setText(String.valueOf(viewedClient.getAge()));

        // Set hospital
        if (viewedClient.getHospital() == null) {
            hospital.setText("Unknown");
        } else {
            hospital.setText(viewedClient.getHospital().getName());
        }

        // Set travel time
        if (donor != null && viewedClient.getHospital() != null && donor.getHospital() != null) {
            Duration timeBetweenHospitals = viewedClient.getHospital().calculateTimeTo(donor.getHospital());
            travelTime.setText(DurationFormatter.getFormattedDuration(timeBetweenHospitals, Format.Biggest));
        } else {
            travelTime.setText("Unknown");
        }

        // Set wait time
        String waitTimeString = "Error: no request";
        List<TransplantRequest> transplantRequests = getClientResolver().getTransplantRequests(viewedClient);
        for (TransplantRequest transplantRequest : transplantRequests) {
            if (transplantRequest.getRequestedOrgan() == organ) {
                Duration waitTime = transplantRequest.getTimeSinceRequest();
                waitTimeString = DurationFormatter.getFormattedDuration(waitTime, Format.Biggest);
            }
        }
        requestedTime.setText(waitTimeString);

        // Set image
        loadImage();

        // Track the adding of panes with the spiderweb pane collection.
        receiverVBox.setOnMouseClicked(mouseEvent ->

        {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 1) {
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

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        viewedClient = windowContext.getViewClient();
        setClientFields();
        refresh();
    }

    @Override
    public void refresh() {
        //TODO
    }

    private void loadImage() {
        byte[] bytes;
        try {
            bytes = getImageManager().getClientImage(viewedClient.getUid());
        } catch (NotFoundException ignored) {
            try {
                bytes = getImageManager().getDefaultImage();
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

    }

}