package com.humanharvest.organz.controller.client;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TableViewMatchers.containsRow;
import static org.testfx.util.NodeQueryUtils.isVisible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Test;

public class ViewProceduresControllerClientTest extends ControllerTest {

    private final Client testClient = new Client(1);
    private final Collection<ProcedureRecord> pastRecords = new ArrayList<>();
    private final Collection<ProcedureRecord> pendingRecords = new ArrayList<>();

    @Override
    protected Page getPage() {
        return Page.VIEW_PROCEDURES;
    }

    @Override
    protected void initState() {
        State.reset();
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

        pastRecords.add(new ProcedureRecord("Summary1", "Description1", LocalDate.of(2000, 1, 1)));
        pastRecords.add(new ProcedureRecord("Summary2", "Description2", LocalDate.of(2000, 2, 1)));
        pastRecords.add(new ProcedureRecord("A Summary 3", "Description3", LocalDate.of(2000, 3, 1)));
        pendingRecords.add(new ProcedureRecord("Summary4", "Description4", LocalDate.of(2045, 1, 1)));

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
