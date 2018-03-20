package seng302.Actions;


import org.junit.Before;
import org.junit.Test;
import seng302.Donor;
import seng302.DonorManager;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DeleteDonorActionTest {

    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor baseDonor;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new DonorManager();
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addDonor(baseDonor);
    }

    @Test
    public void CheckDonorDeletedTest() {
        DeleteDonorAction action = new DeleteDonorAction(baseDonor, manager);
        invoker.execute(action);
        assertEquals(0, manager.getDonors().size());
    }

    @Test
    public void CheckDonorDeletedUndoTest() {
        DeleteDonorAction action = new DeleteDonorAction(baseDonor, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(1, manager.getDonors().size());
    }

    @Test
    public void CheckDonorMultipleDeletesOneUndoTest() {
        Donor second = new Donor("SecondDonor", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addDonor(second);

        DeleteDonorAction action = new DeleteDonorAction(baseDonor, manager);
        DeleteDonorAction secondAction = new DeleteDonorAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(baseDonor, manager.getDonors().get(0));
        assertEquals(1, manager.getDonors().size());
    }

    @Test
    public void CheckDonorMultipleDeletesOneUndoRedoTest() {
        Donor second = new Donor("SecondDonor", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addDonor(second);

        DeleteDonorAction action = new DeleteDonorAction(baseDonor, manager);
        DeleteDonorAction secondAction = new DeleteDonorAction(second, manager);

        invoker.execute(action);
        invoker.execute(secondAction);

        invoker.undo();
        assertEquals(baseDonor, manager.getDonors().get(0));
        assertEquals(1, manager.getDonors().size());

        invoker.redo();

        assertEquals(0, manager.getDonors().size());
    }


}
