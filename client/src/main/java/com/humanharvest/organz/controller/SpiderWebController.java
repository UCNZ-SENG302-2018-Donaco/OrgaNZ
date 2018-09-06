package com.humanharvest.organz.controller;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.PageNavigatorStandard;

public class SpiderWebController extends SubController {

    private Client client;

    public SpiderWebController() {
//        this.client = client;
        try {
//            Stage newStage = new Stage();
//
//            FXMLLoader loader = new FXMLLoader();
//            MainController mainController = loader.getController();
//            mainController.setStage(newStage);
//            Pane mainPane = loader.load(PageNavigatorStandard.class.getResourceAsStream(Page.MAIN.getPath()));
//
//            Scene scene = new Scene(mainPane);
//            newStage.setScene(scene);
//            newStage.show();
//
//            this.client = windowContext.getViewClient();
            Stage newStage = new Stage();
            newStage.setTitle("Organ Client Management System");
            FXMLLoader loader = new FXMLLoader();
            Pane mainPane = loader.load(PageNavigatorStandard.class.getResourceAsStream(Page.MAIN.getPath()));
            MainController mainController = loader.getController();
            mainController.setStage(newStage);
            mainController.setPane(mainPane);
            State.addMainController(mainController);
            newStage.setOnCloseRequest(e -> State.deleteMainController(mainController));

            Scene scene = new Scene(mainPane);
            newStage.setScene(scene);
            newStage.setFullScreen(true);
            newStage.show();

            //display client in center of page

            displayOrgans();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void displayOrgans() {
        // wrap organs in container to display on screen
        for (DonatedOrgan organ: client.getDonatedOrgans()) {
            OrganContainer organContainer = new OrganContainer(organ);

        }
    }

}
