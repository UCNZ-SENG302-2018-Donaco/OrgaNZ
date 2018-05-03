package seng302.Controller.Client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import seng302.Client;
import seng302.Clinician;
import seng302.Controller.ControllerTest;
import seng302.IllnessRecord;
import seng302.ProcedureRecord;
import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.util.NodeQueryUtils;

public class ViewProceduresControllerClinicianTest extends ControllerTest {

    private Client testClient = new Client(1);
    private Clinician testClinician = new Clinician("A", "B", "C", "D", Region.UNSPECIFIED, 0, "E");

    private ProcedureRecord procedure1;
    private ProcedureRecord procedure2;
    private ProcedureRecord procedure3;
    private ProcedureRecord procedure4;
    private List<ProcedureRecord> pastRecords = new ArrayList<>();



    @Override
    protected Page getPage() {
        return Page.VIEW_PROCEDURES;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClinician);
        mainController.setWindowContext(new WindowContext.WindowContextBuilder()
                .setAsClinViewClientWindow()
                .viewClient(testClient)
                .build());
        resetRecords();
    }

    @Before
    public void resetRecords() {
        for (ProcedureRecord record : testClient.getPastProcedures()) {
            testClient.deleteProcedureRecord(record);
        }
        for (ProcedureRecord record : testClient.getPendingProcedures()) {
            testClient.deleteProcedureRecord(record);
        }

        procedure1 = new ProcedureRecord("Summary1", "Description1", LocalDate.of(2000, 10, 12));
        Set<Organ> organs = new HashSet<>();
        organs.add(Organ.KIDNEY);
        organs.add(Organ.LIVER);
        procedure1.setAffectedOrgans(organs);
        procedure2 = new ProcedureRecord("Summary2", "Description2", LocalDate.of(2000, 11, 13));
        organs.add(Organ.HEART);
        procedure2.setAffectedOrgans(organs);
        procedure3 = new ProcedureRecord("A Summary 3", "Description3", LocalDate.of(2000, 12, 14));
        procedure4 = new ProcedureRecord("Summary4", "Description4", LocalDate.of(2045, 10, 15));
        procedure4.setAffectedOrgans(organs);

        pastRecords.add(procedure1);
        pastRecords.add(procedure2);
        pastRecords.add(procedure3);

        testClient.addProcedureRecord(procedure1);
        testClient.addProcedureRecord(procedure2);
        testClient.addProcedureRecord(procedure3);
        testClient.addProcedureRecord(procedure4);
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
                procedure4.getSummary(),
                procedure4.getDate(),
                procedure4.getAffectedOrgans(),
                procedure4.getDescription()));
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
                .anyMatch(illness -> illness.getDescription().equals(toBeAdded.getDescription())));
    }

    @Test@Ignore
    public void setDateFromPastToFutureTest() throws InterruptedException {
        clickOn((Node) lookup(".table-row-cell").nth(2).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("12/10/2000")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("12/10/2000")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("12/10/2000")).query()).write("12/10/2045");
        clickOn("#pastProcedureView");

        Thread.sleep(1000);

        assertEquals(LocalDate.of(2045, 12, 10), procedure1.getDate());
    }

    @Test@Ignore
    public void editSummaryTest() throws InterruptedException {
        clickOn((Node) lookup(".table-row-cell").nth(2).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("Summary1")).query());
        clickOn((Node) lookup(NodeQueryUtils.hasText("Summary1")).query()).write("NewSummary");
        press(KeyCode.ENTER);
        Thread.sleep(1000);
        assertEquals("NewSummary", procedure1.getSummary());
    }

}
