package com.humanharvest.organz.controller.client;

import static com.humanharvest.organz.utilities.DurationFormatter.getFormattedDuration;

import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import com.humanharvest.organz.state.State;
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
    manager = State.getClientManager();
    session = State.getSession();
  }

  /**
   * Initializes the UI for this page.
   */
  private void setClientFields() {
    List<TransplantRequest> transplantRequests = State.getClientResolver()
        .getTransplantRequests(viewedClient);
    viewedTransplantRequest = null;

    Organ organ = Organ.LIVER; // for testing

    for (TransplantRequest transplantRequest : transplantRequests) {
      if (transplantRequest.getRequestedOrgan() == organ) {
        viewedTransplantRequest = viewedClient.getTransplantRequest(Organ.LIVER);
      }
    }

    try {
      Client reciever = viewedTransplantRequest.getClient();
      name.setText(reciever.getFullName());
      hospital.setText(reciever.getHospital().getName());
      travelTime
          .setText(viewedClient.getHospital().calculateTimeTo(reciever.getHospital()).toString());

      loadImage();

      // Track the adding of panes with the spiderweb pane collection.
      receiverVBox.setOnMouseClicked(mouseEvent -> {
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
    } catch (NullPointerException e) {
      PageNavigator.showAlert(AlertType.ERROR, "No Reciever","No Reciever that required Liver",mainController.getStage());

    }

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

  }

}