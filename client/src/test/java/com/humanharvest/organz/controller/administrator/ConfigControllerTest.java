package com.humanharvest.organz.controller.administrator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
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
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Test;

public class ConfigControllerTest extends ControllerTest {

    private final Administrator admin = new Administrator("admin1", "password");
    private final Set<Hospital> hospitals = Hospital.getDefaultHospitals();

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

            for (Organ organ : Organ.values()) {
                hospital.removeTransplantProgramFor(organ);
            }
        }
        State.getConfigManager().setHospitals(hospitalsCopy);

        // Add admin, log in, and open page
        State.getAdministratorManager().addAdministrator(admin);
        State.login(admin);
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

    @Test
    public void addTransplantProgram() {
        clickOn(firstHospital.getName());

        // click on the checkbox for the first organ, then the Apply button
        clickOn((Node) lookup("#organSelector").lookup(".check-box").nth(0).query());
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
