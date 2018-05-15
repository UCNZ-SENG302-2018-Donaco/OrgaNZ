package seng302.Controller.Client;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;
import seng302.State.State;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;
import org.testfx.util.NodeQueryUtils;

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
    private final IllnessRecord[] testCurrentIllnessRecords = {
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
    private Client testClient = new Client(0);

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
        verifyThat("#illnessNameField", isVisible());
    }

    @Test
    public void modifyButtonsEnabledTest() {
        verifyThat("#toggleCuredButton", isVisible());
        verifyThat("#toggleChronicButton", isVisible());
        verifyThat("#deleteButton", isVisible());
    }

    @Test
    public void addNewIllnessTest() {
        testClient.setDateOfBirth(LocalDate.now().minusDays(10000));
        IllnessRecord toBeAdded = new IllnessRecord("diabeetus", LocalDate.now(), null, false);

        clickOn("#illnessNameField").write(toBeAdded.getIllnessName());
        clickOn("Add Illness");

        for (IllnessRecord illness : testClient.getCurrentIllnesses()) {
            System.out.println(illness);
        }
        assertTrue(testClient.getCurrentIllnesses().stream()
                .anyMatch(illness -> illness.getIllnessName().equals(toBeAdded.getIllnessName())));
    }

    @Test
    public void addChronicTagTest() {
        IllnessRecord addChronicTag = testCurrentIllnessRecords[0];
        assertFalse(addChronicTag.isChronic());
        clickOn((Node) lookup(NodeQueryUtils.hasText(addChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertTrue(addChronicTag.isChronic());
    }

    @Test
    public void addChronicTagToPastIllnessTest() {
        IllnessRecord addChronicTag = testPastIllnessRecords[0];
        assertFalse(addChronicTag.isChronic());
        clickOn((Node) lookup(NodeQueryUtils.hasText(addChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");

        // Check it is in current illnesses and not in past illnesses
        verifyThat("#pastIllnessView", not(containsRow(
                addChronicTag.getIllnessName(),
                addChronicTag.getDiagnosisDate(),
                addChronicTag.getCuredDate())));
        verifyThat("#currentIllnessView", containsRow(
                addChronicTag.getIllnessName(),
                addChronicTag.getDiagnosisDate(),
                addChronicTag.isChronic()));

        assertEquals(null, addChronicTag.getCuredDate());
        assertTrue(addChronicTag.isChronic());
    }

    @Test
    public void removeChronicTagTest() {
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        assertTrue(removeChronicTag.isChronic());
        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertFalse(removeChronicTag.isChronic());
    }

    @Test
    public void tryToMovetoPastIllnessesWithChronicTagTest() {
        IllnessRecord chronicIllness = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(NodeQueryUtils.hasText(chronicIllness.getIllnessName())).query());
        assertTrue(chronicIllness.isChronic());
        clickOn("#toggleCuredButton");
        press(KeyCode.ENTER); //close dialog box

        verifyThat("#pastIllnessView", not(containsRow(
                chronicIllness.getIllnessName(),
                chronicIllness.getDiagnosisDate(),
                chronicIllness.getCuredDate())));
        verifyThat("#currentIllnessView", containsRow(
                chronicIllness.getIllnessName(),
                chronicIllness.getDiagnosisDate(),
                chronicIllness.isChronic()));
        assertEquals(chronicIllness.getCuredDate(), null);
        assertTrue(chronicIllness.isChronic());
    }

    @Test
    public void movetoPastIllnessesAfterChronicRemovedTest() {
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertFalse(removeChronicTag.isChronic());

        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleCuredButton");

        verifyThat("#pastIllnessView", containsRow(
                removeChronicTag.getIllnessName(),
                removeChronicTag.getDiagnosisDate(),
                removeChronicTag.getCuredDate()));
        verifyThat("#currentIllnessView", not(containsRow(
                removeChronicTag.getIllnessName(),
                removeChronicTag.getDiagnosisDate(),
                removeChronicTag.isChronic())));
        assertEquals(removeChronicTag.getCuredDate(), LocalDate.now());
    }

    @Test
    public void pastIllnessContainsRecordsTest() {
        for (IllnessRecord record : testPastIllnessRecords) {
            verifyThat("#pastIllnessView", containsRow(
                    record.getIllnessName(),
                    record.getDiagnosisDate(),
                    record.getCuredDate()));
        }
    }

    @Test
    public void currentIllnessContainsRecordsTest() {
        for (IllnessRecord record : testCurrentIllnessRecords) {
            verifyThat("#currentIllnessView", containsRow(
                    record.getIllnessName(),
                    record.getDiagnosisDate(),
                    record.isChronic()));
        }
    }

    @Test
    public void moveIllnessToPastTest() {
        IllnessRecord toBeMoved = testCurrentIllnessRecords[0];

        clickOn((Node) lookup(NodeQueryUtils.hasText(toBeMoved.getIllnessName())).query());
        clickOn("#toggleCuredButton");

        verifyThat("#pastIllnessView", containsRow(
                toBeMoved.getIllnessName(),
                toBeMoved.getDiagnosisDate(),
                toBeMoved.getCuredDate()));
        verifyThat("#currentIllnessView", not(containsRow(
                toBeMoved.getIllnessName(),
                toBeMoved.getDiagnosisDate(),
                toBeMoved.isChronic())));
        assertEquals(toBeMoved.getCuredDate(), LocalDate.now());
    }

    @Test
    public void moveIllnesstoCurrentTest() {
        IllnessRecord toBeMoved = testPastIllnessRecords[0];

        clickOn((Node) lookup(NodeQueryUtils.hasText(toBeMoved.getIllnessName())).query());
        clickOn("#toggleCuredButton");

        verifyThat("#currentIllnessView", containsRow(
                toBeMoved.getIllnessName(),
                toBeMoved.getDiagnosisDate(),
                toBeMoved.isChronic()));
        verifyThat("#pastIllnessView", not(containsRow(
                toBeMoved.getIllnessName(),
                toBeMoved.getDiagnosisDate(),
                toBeMoved.getCuredDate())));
        assertNull(toBeMoved.getCuredDate());
    }

    @Test
    public void deleteIllnessRecordTest() {
        IllnessRecord toBeDeleted = testPastIllnessRecords[0];

        clickOn((Node) lookup(NodeQueryUtils.hasText(toBeDeleted.getIllnessName())).query());
        clickOn("#deleteButton");

        verifyThat("#currentIllnessView", not(containsRow(
                toBeDeleted.getIllnessName(),
                toBeDeleted.getDiagnosisDate(),
                toBeDeleted.isChronic())));
        verifyThat("#pastIllnessView", not(containsRow(
                toBeDeleted.getIllnessName(),
                toBeDeleted.getDiagnosisDate(),
                toBeDeleted.getCuredDate())));
        assertTrue(!testClient.getPastIllnesses().contains(toBeDeleted));
        assertTrue(!testClient.getCurrentIllnesses().contains(toBeDeleted));
    }
}
