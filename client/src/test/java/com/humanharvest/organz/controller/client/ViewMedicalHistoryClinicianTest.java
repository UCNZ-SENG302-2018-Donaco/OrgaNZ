package com.humanharvest.organz.controller.client;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Before;
import org.junit.Test;
import org.testfx.util.NodeQueryUtils;

public class ViewMedicalHistoryClinicianTest extends ControllerTest {

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

    private final Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(),
            Country.NZ, 0,
            "E");
    private final Client testClient = new Client(1);

    @Override
    protected Page getPage() {
        return Page.VIEW_MEDICAL_HISTORY;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testClinician);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
        State.getClientManager().addClient(testClient);
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

        assertTrue(testClient.getCurrentIllnesses().stream()
                .anyMatch(illness -> illness.getIllnessName().equals(toBeAdded.getIllnessName())));
    }

    @Test
    public void addChronicTagTest() {
        IllnessRecord addChronicTag = testCurrentIllnessRecords[0];
        assertFalse(addChronicTag.getIsChronic());
        clickOn((Node) lookup(NodeQueryUtils.hasText(addChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertTrue(addChronicTag.getIsChronic());
    }

    @Test
    public void addChronicTagToPastIllnessTest() {
        IllnessRecord addChronicTag = testPastIllnessRecords[0];
        assertFalse(addChronicTag.getIsChronic());
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
                addChronicTag.getIsChronic()));

        assertNull(addChronicTag.getCuredDate());
        assertTrue(addChronicTag.getIsChronic());
    }

    @Test
    public void removeChronicTagTest() {
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        assertTrue(removeChronicTag.getIsChronic());
        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertFalse(removeChronicTag.getIsChronic());
    }

    @Test
    public void tryToMovetoPastIllnessesWithChronicTagTest() {
        IllnessRecord chronicIllness = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(NodeQueryUtils.hasText(chronicIllness.getIllnessName())).query());
        assertTrue(chronicIllness.getIsChronic());
        clickOn("#toggleCuredButton");
        press(KeyCode.ENTER); //close dialog box

        verifyThat("#pastIllnessView", not(containsRow(
                chronicIllness.getIllnessName(),
                chronicIllness.getDiagnosisDate(),
                chronicIllness.getCuredDate())));
        verifyThat("#currentIllnessView", containsRow(
                chronicIllness.getIllnessName(),
                chronicIllness.getDiagnosisDate(),
                chronicIllness.getIsChronic()));
        assertNull(chronicIllness.getCuredDate());
        assertTrue(chronicIllness.getIsChronic());
    }

    @Test
    public void movetoPastIllnessesAfterChronicRemovedTest() {
        IllnessRecord removeChronicTag = testCurrentIllnessRecords[1];
        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleChronicButton");
        assertFalse(removeChronicTag.getIsChronic());

        clickOn((Node) lookup(NodeQueryUtils.hasText(removeChronicTag.getIllnessName())).query());
        clickOn("#toggleCuredButton");

        verifyThat("#pastIllnessView", containsRow(
                removeChronicTag.getIllnessName(),
                removeChronicTag.getDiagnosisDate(),
                removeChronicTag.getCuredDate()));
        verifyThat("#currentIllnessView", not(containsRow(
                removeChronicTag.getIllnessName(),
                removeChronicTag.getDiagnosisDate(),
                removeChronicTag.getIsChronic())));
        assertEquals(LocalDate.now(), removeChronicTag.getCuredDate());
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
                    record.getIsChronic()));
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
                toBeMoved.getIsChronic())));
        assertEquals(LocalDate.now(), toBeMoved.getCuredDate());
    }

    @Test
    public void moveIllnesstoCurrentTest() {
        IllnessRecord toBeMoved = testPastIllnessRecords[0];

        clickOn((Node) lookup(NodeQueryUtils.hasText(toBeMoved.getIllnessName())).query());
        clickOn("#toggleCuredButton");

        verifyThat("#currentIllnessView", containsRow(
                toBeMoved.getIllnessName(),
                toBeMoved.getDiagnosisDate(),
                toBeMoved.getIsChronic()));
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
                toBeDeleted.getIsChronic())));
        verifyThat("#pastIllnessView", not(containsRow(
                toBeDeleted.getIllnessName(),
                toBeDeleted.getDiagnosisDate(),
                toBeDeleted.getCuredDate())));
        assertTrue(!testClient.getPastIllnesses().contains(toBeDeleted));
        assertTrue(!testClient.getCurrentIllnesses().contains(toBeDeleted));
    }
}
