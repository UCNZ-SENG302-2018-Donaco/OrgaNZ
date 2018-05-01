package seng302.Controller.Client;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

import seng302.Clinician;
import seng302.Controller.Clinician.ClinicianMedicalHistoryController;
import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;

import seng302.Client;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewIllnessHistoryClinicianTest extends ControllerTest {

    private final IllnessRecord[] testPastIllnessRecords = {
            new IllnessRecord(
                    "Influenza",
                    LocalDate.of(2000, 1, 13),
                    LocalDate.of(2005, 2, 15),
                    false
            ),
            new IllnessRecord(
                    "Clinicial Depression",
                    LocalDate.of(2010, 6, 1),
                    LocalDate.of(2012, 5, 7),
                    false
            )
    };
    private final  IllnessRecord[] testCurrentIllnessRecords = {
        new IllnessRecord(
            "Mono",
            LocalDate.of(2011, 9, 22),
            null,
            false
        ),
        new IllnessRecord(
            "Colon Cancer",
            LocalDate.of(2014, 3, 4),
            null,
            true
        ),
        new IllnessRecord(
            "Influenza",
            LocalDate.of(2012, 1, 2),
            null,
            false
        ),
        new IllnessRecord(
            "Lung Cancer",
            LocalDate.of(2011, 9, 22),
            null,
            true
        ),
        new IllnessRecord(
            "Monopoly",
            LocalDate.of(2011, 9, 22),
            null,
            false
        )
    };

    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
    private Client testClient = new Client();

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICAL_HISTORY;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
        resetTestClientIllnessHistory();
    }


    @Before
    public void resetTestClientIllnessHistory() {
        for (IllnessRecord record : testClient.getPastIllnesses()) {
            testClient.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testClient.getCurrentIllnesses()) {
            testClient.deleteIllnessRecord(record);
        }
        for (IllnessRecord record : testPastIllnessRecords) {
            testClient.addIllnessRecord(record);
        }
        for (IllnessRecord record : testCurrentIllnessRecords) {
            testClient.addIllnessRecord(record);
        }
    }

    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pastIllnessView", isVisible());
        verifyThat("#currentIllnessView", isVisible());
    }

    @Test
    public void newIllnessesFieldsVisibleTest() {
        verifyThat("#IllnessField", isVisible());
    }

    @Test
    public void modifyButtonsEnabledTest() {
        verifyThat("#moveToHistoryButton", (Button b) -> !b.isDisabled());
        verifyThat("#moveToCurrentButton", (Button b) -> !b.isDisabled());
        verifyThat("#deleteButton", (Button b) -> !b.isDisabled());
    }




    @Test
    public void removeChronicTag(){
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(hasText(removeChronicTag.toString())).query());
        clickOn("#noLongerChronic");
        assertEquals("Colon Cancer Diagnosed on: 04/03/2014",removeChronicTag.toString());


    }

    @Test
    public void movetoPastIllnessesAfterChronicRemovedTest(){
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(hasText(removeChronicTag.toString())).query());
        clickOn("#noLongerChronic");
        assertEquals("Colon Cancer Diagnosed on: 04/03/2014",removeChronicTag.toString());

        clickOn((Node) lookup(hasText(removeChronicTag.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastIllnessView", hasListCell(removeChronicTag));
        verifyThat("#currentIllnessView", not(hasListCell(removeChronicTag)));
        assertEquals(removeChronicTag.getCuredDate(), LocalDate.now());

    }

    @Test
    public void pastIllnessesContainsRecordsTest() {
        for (IllnessRecord record : testPastIllnessRecords) {
            verifyThat("#pastIllnessView", hasListCell(record));
        }
    }

    @Test
    public void currentIllnessesContainsRecordsTest() {
        for (IllnessRecord record : testCurrentIllnessRecords) {
            verifyThat("#currentIllnessView", hasListCell(record));
        }
    }





    @Test
    public void moveIllnessToPastTest() {
        IllnessRecord toBeMoved = testCurrentIllnessRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastIllnessView", hasListCell(toBeMoved));
        verifyThat("#currentIllnessView", not(hasListCell(toBeMoved)));
        assertEquals(toBeMoved.getCuredDate(), LocalDate.now());
    }

    @Test
    public void moveIllnesstoCurrentTest() {
        IllnessRecord toBeMoved = testPastIllnessRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToCurrentButton");

        verifyThat("#currentIllnessView", hasListCell(toBeMoved));
        verifyThat("#pastIllnessView", not(hasListCell(toBeMoved)));
        assertNull(toBeMoved.getCuredDate());
    }


    @Test
    public void deleteIllnessRecordTest() {
        IllnessRecord toBeDeleted = testPastIllnessRecords[0];

        clickOn((Node) lookup(hasText(toBeDeleted.toString())).query());
        clickOn("#deleteButton");

        verifyThat("#pastIllnessView", not(hasListCell(toBeDeleted)));
        verifyThat("#currentIllnessView", not(hasListCell(toBeDeleted)));
        assertTrue(!testClient.getPastIllnesses().contains(toBeDeleted));
        assertTrue(!testClient.getCurrentIllnesses().contains(toBeDeleted));
    }


}
