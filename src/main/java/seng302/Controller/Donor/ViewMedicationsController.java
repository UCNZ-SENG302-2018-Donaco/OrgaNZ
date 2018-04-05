package seng302.Controller.Donor;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import seng302.Actions.ActionInvoker;
import seng302.Controller.MainController;
import seng302.Controller.SubController;
import seng302.Donor;
import seng302.State.DonorManager;
import seng302.State.Session;
import seng302.State.State;

public class ViewMedicationsController extends SubController {
    private Session session;
    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor donor;

    @FXML
    private Pane sidebarPane;

    public ViewMedicationsController() {
        manager = State.getDonorManager();
        invoker = State.getInvoker();
        session = State.getSession();
    }

    @Override
    public void setup(MainController mainController) {
        super.setup(mainController);
        mainController.loadSidebar(sidebarPane);

        if (session.getLoggedInUserType() == Session.UserType.DONOR) {
            donor = session.getLoggedInDonor();
        } else if (windowContext.isClinViewDonorWindow()) {
            donor = windowContext.getViewDonor();
        }
    }
}
