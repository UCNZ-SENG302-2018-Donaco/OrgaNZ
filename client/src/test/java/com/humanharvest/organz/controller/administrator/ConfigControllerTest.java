package com.humanharvest.organz.controller.administrator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javafx.scene.control.DialogPane;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Test;

public class ConfigControllerTest extends ControllerTest {

    private Administrator admin1 = new Administrator("admin1", "password");
    private Set<Hospital> hospitals = Hospital.getDefaultHospitals();

    @Override
    protected Page getPage() {
        return Page.ADMIN_CONFIG;
    }

    @Override
    protected void initState() {
        State.reset();

        // Setup hospitals
        State.getConfigManager().setHospitals(hospitals);

        // Add admin, log in, and open page
        State.getAdministratorManager().addAdministrator(admin1);
        State.login(admin1);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void hospitalListHasAllHospitals() {
        verifyThat("#hospitalSelector", hasItems(hospitals.size()));
    }

    @Test
    public void hospitalDetailsCanBeOpened() {
        // sort hospitals by name and get first hospital,
        // this ensures that the hospital is visible without scrolling.
        List<Hospital> hospitalList = new ArrayList<>(hospitals);
        hospitalList.sort(Comparator.comparing(Hospital::getName));
        Hospital hospital = hospitals.iterator().next();
        String hospitalName = hospital.getName();

        // Double click on the hospital and check it generates a popup about that hospital.
        doubleClickOn(hospitalName);
        alertDialogHasHeaderAndContainsContent(hospitalName, hospital.getAddress());
    }

    public void alertDialogHasHeaderAndContainsContent(final String expectedHeader, final String expectedContent) {
        final javafx.stage.Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertThat(dialogPane.getContentText(), containsString(expectedContent));
    }


}
