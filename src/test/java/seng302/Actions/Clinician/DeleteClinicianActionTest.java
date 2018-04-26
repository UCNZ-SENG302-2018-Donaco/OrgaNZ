package seng302.Actions.Clinician;


import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Clinician.DeleteClinicianAction;
import seng302.Clinician;
import seng302.State.ClinicianManager;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;

public class DeleteClinicianActionTest {

    private ClinicianManager manager;
    private ActionInvoker invoker;
    private Clinician baseClinician;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClinicianManager();
        baseClinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");
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
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
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
        Clinician second = new Clinician("First2", null, "Last2", "Address", Region.UNSPECIFIED, 2, "pass");
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
