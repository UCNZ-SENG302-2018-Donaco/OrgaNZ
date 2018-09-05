package com.humanharvest.organz.controller;

import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.controller.client.ViewClientController;
import com.humanharvest.organz.utilities.exceptions.ServerRestException;

public class SpiderWebController extends SubController {

    private Client client;

    public SpiderWebController() {
//        this.client = client;
        this.client = windowContext.getViewClient();

        //display client in center of page

        displayOrgans();
    }

    private void displayOrgans() {
        // wrap organs in container to display on screen
        for (DonatedOrgan organ: client.getDonatedOrgans()) {
            OrganContainer organContainer = new OrganContainer(organ);

        }
    }

}
