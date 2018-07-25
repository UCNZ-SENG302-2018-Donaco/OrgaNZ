package com.humanharvest.organz.actions.clinician;

import static org.junit.Assert.assertEquals;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.ClinicianManager;
import com.humanharvest.organz.state.ClinicianManagerMemory;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.Before;
import org.junit.Test;

public class DeleteClinicianActionTest extends BaseTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManagerMemory();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED.name(), null, 1, "pass");
        manager.addClinician(baseClinician);
    }

    @Test
    public void CheckClinicianDeletedTest() {
        DeleteClinicianAction action = new DeleteClinicianAction(baseClinician, manager);
        invoker.execute(action);
        assertEquals(1, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianDeletedUndoTest() {
        DeleteClinicianAction action = new DeleteClinicianAction(baseClinician, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(2, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianMultipleDeletesOneUndoTest() {
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED.name(), null, 2, "pass");
        manager.addClinician(second);

        DeleteClinicianAction action = new DeleteClinicianAction(baseClinician, manager);
        DeleteClinicianAction secondAction = new DeleteClinicianAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getClinicians().get(1));
        assertEquals(2, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianMultipleDeletesOneUndoRedoTest() {
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED.name(), null, 2, "pass");
        manager.addClinician(second);

        DeleteClinicianAction action = new DeleteClinicianAction(baseClinician, manager);
        DeleteClinicianAction secondAction = new DeleteClinicianAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(second, manager.getClinicians().get(1));
        assertEquals(2, manager.getClinicians().size());

        invoker.redo();

        assertEquals(1, manager.getClinicians().size());
    }


}
