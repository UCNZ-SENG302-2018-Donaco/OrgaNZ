package com.humanharvest.organz.controller.administrator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasItems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.DialogPane;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Ignore;
import org.junit.Test;

public class ConfigControllerTest extends ControllerTest {

    private Administrator admin1 = new Administrator("admin1", "password");
    private Set<Hospital> hospitals = Hospital.getDefaultHospitals();

    private Hospital firstHospital;
    private Hospital secondHospital;

    @Override
    protected Page getPage() {
        return Page.ADMIN_CONFIG;
    }

    @Override
    protected void initState() {
        State.reset();

        // Setup hospitals
        Set<Hospital> hospitalsCopy = new HashSet<>(hospitals);
        long i = 1;
        for (Hospital hospital : hospitalsCopy) {
            hospital.setId(i);
            i++;
        }
        State.getConfigManager().setHospitals(hospitalsCopy);

        // Add admin, log in, and open page
        State.getAdministratorManager().addAdministrator(admin1);
        State.login(admin1);
        mainController.setWindowContext(WindowContext.defaultContext());

        // sort hospitals by name and get first hospital,
        // this ensures that the hospital is visible without scrolling.
        List<Hospital> hospitalList = new ArrayList<>(hospitals);
        hospitalList.sort(Comparator.comparing(Hospital::getName));
        firstHospital = hospitalList.get(0);
        secondHospital = hospitalList.get(1);
    }

    @Test
    public void hospitalListHasAllHospitals() {
        verifyThat("#hospitalSelector", hasItems(hospitals.size()));
    }

    @Test
    public void hospitalDetailsCanBeOpened() {
        String hospitalName = firstHospital.getName();

        // Double click on the hospital and check it generates a popup about that hospital.
        doubleClickOn(hospitalName);
        alertDialogHasHeaderAndContainsContent(hospitalName, firstHospital.getAddress());
    }

    /**
     * Asserts that header is expectedHeader and content contains expectedContent.
     */
    private void alertDialogHasHeaderAndContainsContent(final String expectedHeader, final String expectedContent) {
        final javafx.stage.Stage actualAlertDialog = getTopModalStage();
        assertNotNull(actualAlertDialog);

        final DialogPane dialogPane = (DialogPane) actualAlertDialog.getScene().getRoot();
        assertEquals(expectedHeader, dialogPane.getHeaderText());
        assertThat(dialogPane.getContentText(), containsString(expectedContent));
    }

    @Ignore
    @Test
    public void addTransplantProgram() {
        clickOn(firstHospital.getName());

        // click on the checkbox for the first organ, then the Apply button
        clickOn((Node) lookup("#organSelector").lookup(".check-box").nth(0).query());
        clickOn("Apply");
        assertThatHospitalOnlyContainsFirstOrgan(firstHospital);
    }

    @Ignore
    @Test
    public void addTransplantProgramThenSelectAnotherHospital() {
        // click on the first hospital, and select the first organ
        clickOn(firstHospital.getName());
        clickOn((Node) lookup("#organSelector").lookup(".check-box").nth(0).query());

        // click on the second hospital, then click apply
        clickOn(secondHospital.getName());
        clickOn("Apply");
        assertThatHospitalOnlyContainsFirstOrgan(firstHospital);
    }

    @Test
    public void organChecksAreMaintainedAfterClickingOnAnotherHospital() {
        // click on the first hospital, and select the first organ
        clickOn(firstHospital.getName());
        clickOn((Node) lookup("#organSelector").lookup(".check-box").nth(0).query());

        // click on the second hospital, then the first again, then uncheck the first organ, then click apply
        clickOn(secondHospital.getName());
        clickOn(firstHospital.getName());
        clickOn((Node) lookup("#organSelector").lookup(".check-box").nth(0).query());
        clickOn("Apply");

        assertEquals(0,
                State.getConfigManager().getHospitalById(firstHospital.getId()).get().getTransplantPrograms().size());
    }

    /**
     * Check that the hospital contains the first organ and nothing else
     */
    private void assertThatHospitalOnlyContainsFirstOrgan(Hospital hospital) {
        assertTrue(State.getConfigManager().getHospitalById(hospital.getId()).get().getTransplantPrograms()
                .contains(Organ.values()[0]));
        assertEquals(1,
                State.getConfigManager().getHospitalById(hospital.getId()).get().getTransplantPrograms().size());

    }

}
