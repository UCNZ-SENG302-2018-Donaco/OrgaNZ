package seng302.Actions.Donor;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import seng302.Actions.ActionInvoker;
import seng302.Donor;
import seng302.State.DonorManager;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ModifyDonorOrgansActionTest {

    private ActionInvoker invoker;
    private Donor baseDonor;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        DonorManager manager = new DonorManager();
        baseDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addDonor(baseDonor);
    }

    @Test
    public void UpdateSingleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        assertTrue(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateMultipleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.BONE, true);
        invoker.execute(action);
        assertTrue(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertTrue(baseDonor.getOrganStatus().get(Organ.PANCREAS));
        assertTrue(baseDonor.getOrganStatus().get(Organ.BONE));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateMultipleOrganValidUndoTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.undo();

        assertFalse(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.PANCREAS));
        assertFalse(baseDonor.getOrganStatus().get(Organ.BONE));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateTwoSeparateValidUndoTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        ModifyDonorOrgansAction action2 = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action2.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.undo();

        assertTrue(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.PANCREAS));
        assertFalse(baseDonor.getOrganStatus().get(Organ.BONE));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateTwoSeparateValidUndoUndoTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        ModifyDonorOrgansAction action2 = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action2.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.undo();
        invoker.undo();

        assertFalse(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.PANCREAS));
        assertFalse(baseDonor.getOrganStatus().get(Organ.BONE));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateThreeSeparateValidUndoUndoRedoTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        ModifyDonorOrgansAction action2 = new ModifyDonorOrgansAction(baseDonor);
        ModifyDonorOrgansAction action3 = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action3.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertTrue(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertTrue(baseDonor.getOrganStatus().get(Organ.PANCREAS));
        assertFalse(baseDonor.getOrganStatus().get(Organ.BONE));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateSingleOrganTrueThenFalseTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyDonorOrgansAction action2 = new ModifyDonorOrgansAction(baseDonor);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        assertFalse(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateSingleOrganTrueThenFalseUndoOneTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyDonorOrgansAction action2 = new ModifyDonorOrgansAction(baseDonor);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        invoker.undo();

        assertTrue(baseDonor.getOrganStatus().get(Organ.LIVER));
        assertFalse(baseDonor.getOrganStatus().get(Organ.HEART));
    }

    @Test
    public void CheckExecuteTextOneChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Changed organ registration for client %d: First Last:\n"
                + "\n"
                + "Registered Liver for donation.", baseDonor.getUid());

        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);

        action.addChange(Organ.LIVER, true);
        String result = invoker.execute(action);
        assertEquals(expectedText, result);
    }

    @Test(expected = OrganAlreadyRegisteredException.class)
    public void CheckAddExistingTest() throws OrganAlreadyRegisteredException {
        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);
        baseDonor.setOrganStatus(Organ.LIVER, true);

        action.addChange(Organ.LIVER, true);
    }

    @Test
    public void CheckExecuteExistingTest() throws OrganAlreadyRegisteredException {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);

        action.addChange(Organ.LIVER, true);

        baseDonor.setOrganStatus(Organ.LIVER, true);

        invoker.execute(action);

        assertTrue(errContent.toString().contains("OrganAlreadyRegisteredException"));
    }

    @Test
    public void CheckExecuteTextThreeChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Changed organ registration for client %d: First Last:\n"
                + "\n"
                + "Registered Heart for donation.\n"
                + "Registered Liver for donation.\n"
                + "Registered Pancreas for donation.", baseDonor.getUid());

        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);

        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.HEART, true);
        String result = invoker.execute(action);
        assertEquals(expectedText, result);
    }

    @Test
    public void CheckUnexecuteTextOneChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Reversed these changes to organ registration for client %d: First "
                + "Last:\n"
                + "\n"
                + "Registered Liver for donation.", baseDonor.getUid());

        ModifyDonorOrgansAction action = new ModifyDonorOrgansAction(baseDonor);

        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        String result = invoker.undo();
        assertEquals(expectedText, result);
    }
}
