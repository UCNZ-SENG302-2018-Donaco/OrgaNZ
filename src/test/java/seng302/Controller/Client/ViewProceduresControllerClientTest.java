package seng302.Controller.Client;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.control.ListViewMatchers.hasListCell;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.MedicationRecord;
import seng302.ProcedureRecord;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.PageNavigator;
import seng302.Utilities.View.WindowContext;

import org.junit.Before;
import org.junit.Test;

public class ViewProceduresControllerClientTest extends ControllerTest {

    private Client testClient = new Client(1);
    private List<ProcedureRecord> pastRecords = new ArrayList<>();
    private List<ProcedureRecord> pendingRecords = new ArrayList<>();


    @Override
    protected Page getPage() {
        return Page.VIEW_PROCEDURES;
    }

    @Override
    protected void initState() {
        State.init();
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
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

        pastRecords.add(new ProcedureRecord("Summary1", "Description1", LocalDate.of(2000, 01, 01)));
        pastRecords.add(new ProcedureRecord("Summary2", "Description2", LocalDate.of(2000, 02, 01)));
        pastRecords.add(new ProcedureRecord("A Summary 3", "Description3", LocalDate.of(2000, 03, 01)));
        pendingRecords.add(new ProcedureRecord("Summary4", "Description4", LocalDate.of(2045, 01, 01)));

        for (ProcedureRecord record : pastRecords) {
            testClient.addProcedureRecord(record);
        }
        for (ProcedureRecord record : pendingRecords) {
            testClient.addProcedureRecord(record);
        }
    }


    @Test
    public void bothListViewsVisibleTest() {
        verifyThat("#pendingProcedureView", isVisible());
        verifyThat("#pastProcedureView", isVisible());
    }

    @Test
    public void addAreaNotVisibleTest() {
        verifyThat("#newProcedurePane", isVisible().negate());
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
        for (ProcedureRecord record : pendingRecords) {
            verifyThat("#pendingProcedureView", containsRow(
                    record.getSummary(),
                    record.getDate(),
                    record.getAffectedOrgans(),
                    record.getDescription()));
        }
    }

}
