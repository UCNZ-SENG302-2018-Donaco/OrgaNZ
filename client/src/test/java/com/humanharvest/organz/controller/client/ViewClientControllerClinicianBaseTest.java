package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class ViewClientControllerClinicianBaseTest extends ControllerTest {

    LocalDate dateOfBirth = LocalDate.now().minusYears(10);
    LocalDate dateOfDeath = LocalDate.now().minusYears(1);
    LocalTime timeOfDeath = LocalTime.parse("10:00:00");
    int futureYear = LocalDate.now().plusYears(2).getYear();
    int recentYear = LocalDate.now().minusYears(2).getYear();
    Client testClient;

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();
        setClientDetails();
        State.getClientManager().addClient(testClient);
        State.login(State.getClinicianManager().getDefaultClinician()); // login as default clinician
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    public abstract void setClientDetails();
}
