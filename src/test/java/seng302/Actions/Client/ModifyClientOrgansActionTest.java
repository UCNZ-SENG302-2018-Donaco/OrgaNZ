package seng302.Actions.Client;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ModifyClientOrgansAction;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.Utilities.Enums.Gender;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ModifyClientOrgansActionTest {

    private ActionInvoker invoker;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        ClientManager manager = new ClientManager();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(baseClient);
    }

    @Test
    public void UpdateSingleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateMultipleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.BONE, true);
        invoker.execute(action);
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.PANCREAS));
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateMultipleOrganValidUndoTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.undo();

        assertFalse(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.PANCREAS));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateTwoSeparateValidUndoTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action2.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.undo();

        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.PANCREAS));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateTwoSeparateValidUndoUndoTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action2.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.undo();
        invoker.undo();

        assertFalse(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.PANCREAS));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateThreeSeparateValidUndoUndoRedoTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient);
        ModifyClientOrgansAction action3 = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        action2.addChange(Organ.PANCREAS, true);
        action3.addChange(Organ.BONE, true);

        invoker.execute(action);
        invoker.execute(action2);
        invoker.execute(action3);
        invoker.undo();
        invoker.undo();
        invoker.redo();

        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.PANCREAS));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.BONE));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }


    @Test
    public void UpdateSingleOrganTrueThenFalseTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        assertFalse(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateSingleOrganTrueThenFalseUndoOneTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        invoker.undo();

        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void CheckExecuteTextOneChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Changed organ donation registration for client %d: First Last:\n"
                + "\n"
                + "Registered Liver for donation.", baseClient.getUid());

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);

        action.addChange(Organ.LIVER, true);
        String result = invoker.execute(action);
        assertEquals(expectedText, result);
    }

    @Test(expected = OrganAlreadyRegisteredException.class)
    public void CheckAddExistingTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);
        baseClient.setOrganDonationStatus(Organ.LIVER, true);

        action.addChange(Organ.LIVER, true);
    }

    @Test
    public void CheckExecuteExistingTest() throws OrganAlreadyRegisteredException {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);

        action.addChange(Organ.LIVER, true);

        baseClient.setOrganDonationStatus(Organ.LIVER, true);

        invoker.execute(action);

        assertTrue(errContent.toString().contains("OrganAlreadyRegisteredException"));
    }

    @Test
    public void CheckExecuteTextThreeChangeTest() throws OrganAlreadyRegisteredException {
        final List<String> expectedText = Arrays.asList(
                String.format("Changed organ donation registration for client %d: First Last:", baseClient.getUid()),
                "Registered Heart for donation.",
                "Registered Liver for donation.",
                "Registered Pancreas for donation.");

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);

        action.addChange(Organ.LIVER, true);
        action.addChange(Organ.PANCREAS, true);
        action.addChange(Organ.HEART, true);
        String result = invoker.execute(action);

        for (String expectedLine : expectedText) {
            assertTrue(result.contains(expectedLine));
        }
    }

    @Test
    public void CheckUnexecuteTextOneChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Reversed these changes to organ donation registration for client "
                + "%d: First "
                + "Last:\n"
                + "\n"
                + "Registered Liver for donation.", baseClient.getUid());

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient);

        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        String result = invoker.undo();
        assertEquals(expectedText, result);
    }
}
