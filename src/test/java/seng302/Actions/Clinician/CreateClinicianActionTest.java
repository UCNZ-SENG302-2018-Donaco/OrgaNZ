package seng302.Actions.Clinician;


import static org.junit.Assert.assertEquals;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.State.ClinicianManager;
import seng302.State.ClinicianManagerMemory;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;

public class CreateClinicianActionTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManagerMemory();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
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
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
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
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
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
