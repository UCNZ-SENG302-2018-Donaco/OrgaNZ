package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.controller.components.OrganCheckComboBoxCell;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;

import org.junit.Test;
import org.testfx.util.NodeQueryUtils;

public class ViewProceduresControllerClinicianTest extends ControllerTest {

    private final static Organ[] organs = Organ.values();

    private final int futureYear = LocalDate.now().plusYears(2).getYear();

    private final ProcedureRecord[] pastRecords = {
            new ProcedureRecord("A Summary 1", "Description1", LocalDate.of(2000, 10, 19)),
            new ProcedureRecord("B Summary 2", "Description2", LocalDate.of(2000, 11, 13)),
            new ProcedureRecord("C Summary 3", "Description3", LocalDate.of(2000, 12, 14))

    };
    private final ProcedureRecord[] pendingRecords = {
            new ProcedureRecord("Summary4", "Description4", LocalDate.of(futureYear, 10, 15))
    };
    private final Client testClient = new Client("Alex", null, "Tester", LocalDate.of(1998, 5, 9), 1);
    private final Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED.toString(),
            Country.NZ, 0,
            "");

    @Override
    protected Page getPage() {
        return Page.VIEW_PROCEDURES;
    }

    @Override
    protected void initState() {
        State.reset();
        resetRecords();
        State.login(testClinician);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinicianViewClientWindow()
                .viewClient(testClient)
                .build());
    }

    private void resetRecords() {

        Set<Organ> organs = new HashSet<>();
        organs.add(Organ.BONE_MARROW);
        organs.add(Organ.BONE);
        pastRecords[0].setAffectedOrgans(organs);
        organs.add(Organ.CONNECTIVE_TISSUE);
        pastRecords[1].setAffectedOrgans(organs);
        pendingRecords[0].setAffectedOrgans(organs);

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            testClient.deleteProcedureRecord(record);
        }
        for (ProcedureRecord record : testClient.getPendingProcedures()) {
            testClient.deleteProcedureRecord(record);
        }

        for (ProcedureRecord record : pastRecords) {
            testClient.addProcedureRecord(record);
        }
        for (ProcedureRecord record : pendingRecords) {
            testClient.addProcedureRecord(record);
        }
        State.getClientManager().applyChangesTo(testClient);
    }

    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pendingProcedureView", isVisible());
        verifyThat("#pastProcedureView", isVisible());
    }

    @Test
    public void addAreaVisibleTest() {
        verifyThat("#newProcedurePane", isVisible());
    }

    @Test
    public void pastMedicationsContainsRecordsTest() {

        for (ProcedureRecord record : pastRecords) {
            verifyThat("#pastProcedureView", containsRow(
                    record.getSummary(),
                    record.getDate(),
                    record.getAffectedOrgans(),
                    record.getDescription()));
        }
    }

    @Test
    public void currentMedicationsContainsRecordsTest() {
        verifyThat("#pendingProcedureView", containsRow(
                pendingRecords[0].getSummary(),
                pendingRecords[0].getDate(),
                pendingRecords[0].getAffectedOrgans(),
                pendingRecords[0].getDescription()));
    }

    @Test
    public void addNewProcedureTest() {
        testClient.setDateOfBirth(LocalDate.now().minusDays(10000));
        ProcedureRecord toBeAdded = new ProcedureRecord("SummaryNew", "Desc New", LocalDate.of(2017, 12, 20));

        clickOn("#summaryField").write(toBeAdded.getSummary());
        clickOn("#dateField").write("20/12/2001");
        clickOn("#descriptionField").write(toBeAdded.getDescription());
        clickOn("#affectedOrgansField");
        clickOn((Node) lookup(".check-box").nth(3).query());

        clickOn("Add Procedure");

        assertTrue(testClient.getPastProcedures().stream()
                .anyMatch(record -> record.getDescription().equals(toBeAdded.getDescription())));
    }

    // Editing an existing procedure - date

    @Test
    public void setDateFromPastToFutureTest() {
        clickOn((Node) lookup(NodeQueryUtils.hasText("19/10/2000")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("19/10/2000")).query());
        String futureDate = "19/10/" + futureYear;
        clickOn((Node) lookup(NodeQueryUtils.hasText("19/10/2000")).query()).write(futureDate);
        press(KeyCode.ENTER);

        assertEquals(LocalDate.of(futureYear, 10, 19), pastRecords[0].getDate());
        assertEquals(2, testClient.getPendingProcedures().size());
        verifyThat("#pendingProcedureView", containsRow(
                pastRecords[0].getSummary(),
                pastRecords[0].getDate(),
                pastRecords[0].getAffectedOrgans(),
                pastRecords[0].getDescription()));
    }

    @Test
    public void failToClearDateTest() {
        doubleClickOn((Node) lookup(NodeQueryUtils.hasText("19/10/2000")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("19/10/2000")).query());
        press(KeyCode.DELETE);
        press(KeyCode.ENTER);

        assertEquals(LocalDate.of(2000, 10, 19), pastRecords[0].getDate()); // unchanged
        assertEquals(3, testClient.getPastProcedures().size()); // still has 3 past procedures
        verifyThat("#pendingProcedureView", containsRow(
                pendingRecords[pendingRecords.length - 1].getSummary(),
                pendingRecords[pendingRecords.length - 1].getDate(),
                pendingRecords[pendingRecords.length - 1].getAffectedOrgans(),
                pendingRecords[pendingRecords.length - 1].getDescription()));
    }

    // Editing an existing procedure - summary

    @Test
    public void editSummaryTest() {
        clickOn((Node) lookup(NodeQueryUtils.hasText("A Summary 1")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("A Summary 1")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("A Summary 1")).query()).write("NewSummary");
        press(KeyCode.ENTER);

        assertEquals("NewSummary", pastRecords[0].getSummary());
    }

    @Test
    public void failToClearSummaryTest() {
        doubleClickOn((Node) lookup(NodeQueryUtils.hasText("A Summary 1")).query());
        press(KeyCode.DELETE);
        press(KeyCode.ENTER);

        assertEquals("A Summary 1", pastRecords[0].getSummary());
    }

    // Editing an existing procedure - description

    @Test
    public void editDescriptionTest() {
        clickOn((Node) lookup(NodeQueryUtils.hasText("Description1")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("Description1")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("Description1")).query()).write("NewDescription");
        press(KeyCode.ENTER);

        assertEquals("NewDescription", pastRecords[0].getDescription());
    }

    // Editing an existing procedure - organs

    @Test
    public void addOrganSingleTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(0).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            if ("C Summary 3".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[1]);
                assertEquals(affected, record.getAffectedOrgans());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    @Test
    public void addOrganMultipleTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(0).query());

        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(2).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(4).query());

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            if ("C Summary 3".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[1]);
                affected.add(organs[2]);
                affected.add(organs[4]);
                assertEquals(affected, record.getAffectedOrgans());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    @Test
    public void removeOrganSingleTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            if ("B Summary 2".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[0]);
                affected.add(organs[3]);
                assertEquals(affected, record.getAffectedOrgans());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    @Test
    public void removeOrganMultipleTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(0).query());

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            if ("B Summary 2".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[3]);
                assertEquals(affected, record.getAffectedOrgans());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    @Test
    public void toggleMultipleOrgansTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(0).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(2).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(3).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(5).query());

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        for (ProcedureRecord record : testClient.getPastProcedures()) {
            if ("B Summary 2".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[2]);
                affected.add(organs[5]);
                assertEquals(affected, record.getAffectedOrgans());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    @Test
    public void toggleMultipleOrgansThenChangeFromPastToPendingTest() {
        clickOn((Node) lookup((OrganCheckComboBoxCell<?> o) -> true).nth(1).query());

        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(0).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(1).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(2).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(3).query());
        clickOn((Node) lookup((CheckBox checkBox) -> true).nth(5).query());

        clickOn((Node) lookup(NodeQueryUtils.hasText("13/11/2000")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("13/11/2000")).query());
        String futureDate = "19/10/" + futureYear;
        clickOn((Node) lookup(NodeQueryUtils.hasText("13/11/2000")).query()).write(futureDate);
        press(KeyCode.ENTER);

        clickOn("#pendingProcedureView");

        boolean hasMatch = false;

        assertEquals(2, testClient.getPendingProcedures().size());

        for (ProcedureRecord record : testClient.getPendingProcedures()) {
            if ("B Summary 2".equals(record.getSummary())) {
                Collection<Organ> affected = new HashSet<>();
                affected.add(organs[2]);
                affected.add(organs[5]);
                assertEquals(affected, record.getAffectedOrgans());
                assertEquals(LocalDate.of(futureYear, 10, 19), record.getDate());
                hasMatch = true;
                break;
            }
        }

        assertTrue(hasMatch);
    }

    // Deleting an existing procedure

    @Test
    public void deletePendingTest() {

        clickOn((Node) lookup(NodeQueryUtils.hasText("A Summary 1")).query());

        clickOn("#deleteButton");

        assertEquals(2, testClient.getPastProcedures().size());

        verifyThat("#pastProcedureView", containsRow(
                pastRecords[1].getSummary()));
        verifyThat("#pastProcedureView", containsRow(
                pastRecords[2].getSummary()));
    }
}