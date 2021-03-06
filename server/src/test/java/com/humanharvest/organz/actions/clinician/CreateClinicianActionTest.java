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

public class CreateClinicianActionTest extends BaseTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManagerMemory();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED.name(), null, 1, "pass");
    }

    @Test
    public void CheckClinicianAddedTest() {
        CreateClinicianAction action = new CreateClinicianAction(baseClinician, manager);
        invoker.execute(action);
        assertEquals(2, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianAddedUndoTest() {
        CreateClinicianAction action = new CreateClinicianAction(baseClinician, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(1, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianMultipleAddsOneUndoTest() {
        CreateClinicianAction action = new CreateClinicianAction(baseClinician, manager);
        invoker.execute(action);
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED.name(), null, 2,
                "pass");
        CreateClinicianAction secondAction = new CreateClinicianAction(second, manager);
        invoker.execute(secondAction);
        invoker.undo();
        assertEquals(baseClinician, manager.getClinicians().get(1));
        assertEquals(2, manager.getClinicians().size());
    }

    @Test
    public void CheckClinicianMultipleAddsOneUndoRedoTest() {
        CreateClinicianAction action = new CreateClinicianAction(baseClinician, manager);
        invoker.execute(action);
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED.name(), null, 2,
                "pass");
        CreateClinicianAction secondAction = new CreateClinicianAction(second, manager);
        invoker.execute(secondAction);
        invoker.undo();

        assertEquals(baseClinician, manager.getClinicians().get(1));
        assertEquals(2, manager.getClinicians().size());

        invoker.redo();

        assertEquals(baseClinician, manager.getClinicians().get(1));
        assertEquals(second, manager.getClinicians().get(2));
        assertEquals(3, manager.getClinicians().size());
    }
}
