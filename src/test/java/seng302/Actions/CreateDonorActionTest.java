package seng302.Actions;


import org.junit.Before;
import org.junit.Test;
import seng302.Donor;
import seng302.DonorManager;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class CreateDonorActionTest {

    private DonorManager manager;
    private ActionInvoker invoker;
    private Donor baseDonor;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new DonorManager(invoker);
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void CheckDonorAddedTest() {
        CreateDonorAction action = new CreateDonorAction(baseDonor, manager);
        invoker.execute(action);
        assertEquals(1, manager.getDonors().size());
    }

    @Test
    public void CheckDonorAddedUndoTest() {
        CreateDonorAction action = new CreateDonorAction(baseDonor, manager);
        invoker.execute(action);
        invoker.undo();
        assertEquals(0, manager.getDonors().size());
    }

    @Test
    public void CheckDonorMultipleAddsOneUndoTest() {
        CreateDonorAction action = new CreateDonorAction(baseDonor, manager);
        invoker.execute(action);
        Donor second = new Donor("SecondDonor", null, "Last", LocalDate.of(1970, 1, 1), 1);
        CreateDonorAction secondAction = new CreateDonorAction(second, manager);
        invoker.execute(secondAction);
        invoker.undo();
        assertEquals(baseDonor, manager.getDonors().get(0));
        assertEquals(1, manager.getDonors().size());
    }

    @Test
    public void CheckDonorMultipleAddsOneUndoRedoTest() {
        CreateDonorAction action = new CreateDonorAction(baseDonor, manager);
        invoker.execute(action);
        Donor second = new Donor("SecondDonor", null, "Last", LocalDate.of(1970, 1, 1), 1);
        CreateDonorAction secondAction = new CreateDonorAction(second, manager);
        invoker.execute(secondAction);
        invoker.undo();

        assertEquals(baseDonor, manager.getDonors().get(0));
        assertEquals(1, manager.getDonors().size());

        invoker.redo();

        assertEquals(baseDonor, manager.getDonors().get(1));
        assertEquals(2, manager.getDonors().size());
    }


}
