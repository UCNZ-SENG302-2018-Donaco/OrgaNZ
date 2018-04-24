package seng302.Controller.Client;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;

import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.Client;
import seng302.MedicationRecord;
import seng302.State.Session.UserType;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewMedicationsControllerClinicianTest extends ControllerTest {

    private final MedicationRecord[] testPastMedicationRecords = {
            new MedicationRecord(
                    "Med A",
                    LocalDate.of(2000, 1, 13),
                    LocalDate.of(2005, 2, 15)
            ),
            new MedicationRecord(
                    "Med B",
                    LocalDate.of(2010, 6, 1),
                    LocalDate.of(2012, 5, 7)
            )
    };
    private final MedicationRecord[] testCurrentMedicationRecords = {
            new MedicationRecord(
                    "Med C",
                    LocalDate.of(2014, 3, 4),
                    null
            )
    };

    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");
    private Client testClient = new Client();

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICATIONS;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(UserType.CLINICIAN, testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
        resetTestClientMedicationHistory();
    }

    @Before
    public void resetTestClientMedicationHistory() {
        for (MedicationRecord record : testClient.getPastMedications()) {
            testClient.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testClient.getCurrentMedications()) {
            testClient.deleteMedicationRecord(record);
        }
        for (MedicationRecord record : testPastMedicationRecords) {
            testClient.addMedicationRecord(record);
        }
        for (MedicationRecord record : testCurrentMedicationRecords) {
            testClient.addMedicationRecord(record);
        }
    }

    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pastMedicationsView", isVisible());
        verifyThat("#currentMedicationsView", isVisible());
    }

    @Test
    public void newMedicationFieldsVisibleTest() {
        verifyThat("#newMedField", isVisible());
    }

    @Test
    public void modifyButtonsEnabledTest() {
        verifyThat("#moveToHistoryButton", (Button b) -> !b.isDisabled());
        verifyThat("#moveToCurrentButton", (Button b) -> !b.isDisabled());
        verifyThat("#deleteButton", (Button b) -> !b.isDisabled());
    }

    @Test
    public void pastMedicationsContainsRecordsTest() {
        for (MedicationRecord record : testPastMedicationRecords) {
            verifyThat("#pastMedicationsView", hasListCell(record));
        }
    }

    @Test
    public void currentMedicationsContainsRecordsTest() {
        for (MedicationRecord record : testCurrentMedicationRecords) {
            verifyThat("#currentMedicationsView", hasListCell(record));
        }
    }

    @Test
    public void addNewMedicationWithButtonTest() {
        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);

        clickOn("#newMedField").write(toBeAdded.getMedicationName());
        clickOn("Add Medication");

        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
    }

    @Test
    public void addNewMedicationWithEnterTest() {
        MedicationRecord toBeAdded = new MedicationRecord("Med D", LocalDate.now(), null);

        clickOn("#newMedField").write(toBeAdded.getMedicationName());
        type(KeyCode.ENTER);

        verifyThat("#currentMedicationsView", hasListCell(toBeAdded));
    }

    @Test
    public void moveMedicationToPastTest() {
        MedicationRecord toBeMoved = testCurrentMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToHistoryButton");

        verifyThat("#pastMedicationsView", hasListCell(toBeMoved));
        verifyThat("#currentMedicationsView", not(hasListCell(toBeMoved)));
        assertEquals(toBeMoved.getStopped(), LocalDate.now());
    }

    @Test
    public void moveMedicationToCurrentTest() {
        MedicationRecord toBeMoved = testPastMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeMoved.toString())).query());
        clickOn("#moveToCurrentButton");

        verifyThat("#currentMedicationsView", hasListCell(toBeMoved));
        verifyThat("#pastMedicationsView", not(hasListCell(toBeMoved)));
        assertNull(toBeMoved.getStopped());
    }

    @Test
    public void deleteMedicationRecordTest() {
        MedicationRecord toBeDeleted = testPastMedicationRecords[0];

        clickOn((Node) lookup(hasText(toBeDeleted.toString())).query());
        clickOn("#deleteButton");

        verifyThat("#pastMedicationsView", not(hasListCell(toBeDeleted)));
        verifyThat("#currentMedicationsView", not(hasListCell(toBeDeleted)));
        assertTrue(!testClient.getPastMedications().contains(toBeDeleted));
        assertTrue(!testClient.getCurrentMedications().contains(toBeDeleted));
    }
}
