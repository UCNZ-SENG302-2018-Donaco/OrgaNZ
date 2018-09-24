package com.humanharvest.organz.actions.client;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.organ.ModifyClientOrgansAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.ClientManagerMemory;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;

import org.junit.Before;
import org.junit.Test;

public class ModifyClientOrgansActionTest extends BaseTest {

    private ActionInvoker invoker;
    private ClientManager manager;
    private Client baseClient;

    @Before
    public void init() {
        invoker = new ActionInvoker();
        manager = new ClientManagerMemory();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(baseClient);
    }

    @Test
    public void UpdateSingleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateMultipleOrganValidTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
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
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
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
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient, manager);
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
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient, manager);
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
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient, manager);
        ModifyClientOrgansAction action3 = new ModifyClientOrgansAction(baseClient, manager);
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
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient, manager);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        assertFalse(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void UpdateSingleOrganTrueThenFalseUndoOneTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        action.addChange(Organ.LIVER, true);
        invoker.execute(action);

        ModifyClientOrgansAction action2 = new ModifyClientOrgansAction(baseClient, manager);
        action2.addChange(Organ.LIVER, false);
        invoker.execute(action2);

        invoker.undo();

        assertTrue(baseClient.getOrganDonationStatus().get(Organ.LIVER));
        assertFalse(baseClient.getOrganDonationStatus().get(Organ.HEART));
    }

    @Test
    public void CheckExecuteTextOneChangeTest() throws OrganAlreadyRegisteredException {
        final String expectedText = String.format("Changed organ donation registration for client %d: First Last:%n"
                + "%n"
                + "Registered Liver for donation.", baseClient.getUid());

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);

        action.addChange(Organ.LIVER, true);
        String result = invoker.execute(action);
        assertEquals(expectedText, result);
    }

    @Test(expected = OrganAlreadyRegisteredException.class)
    public void CheckAddExistingTest() throws OrganAlreadyRegisteredException {
        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);
        baseClient.setOrganDonationStatus(Organ.LIVER, true);

        action.addChange(Organ.LIVER, true);
    }

    @Test
    public void CheckExecuteExistingTest() throws OrganAlreadyRegisteredException {
        // We need to hook in to the logger to check that the event has been logged
        final Logger logger = Logger.getLogger(ModifyClientOrgansAction.class.getName());
        List<LogRecord> logRecords = new ArrayList<>();
        logger.addHandler(new Handler() {
            // When a log is added, put it in our list to check at the end
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);

        // Add the action, which is valid as the liver is not yet registered
        action.addChange(Organ.LIVER, true);

        // Register the organ, so now the action is invalid
        baseClient.setOrganDonationStatus(Organ.LIVER, true);

        invoker.execute(action);

        // Check that this was detected and logged
        assertEquals(1, logRecords.size());
        assertEquals("Liver is already registered for donation", logRecords.get(0).getMessage());
    }

    @Test
    public void CheckExecuteTextThreeChangeTest() throws OrganAlreadyRegisteredException {
        final List<String> expectedText = Arrays.asList(
                String.format("Changed organ donation registration for client %d: First Last:", baseClient.getUid()),
                "Registered Heart for donation.",
                "Registered Liver for donation.",
                "Registered Pancreas for donation.");

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);

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
                + "Last:%n"
                + "%n"
                + "Registered Liver for donation.", baseClient.getUid());

        ModifyClientOrgansAction action = new ModifyClientOrgansAction(baseClient, manager);

        action.addChange(Organ.LIVER, true);
        invoker.execute(action);
        String result = invoker.undo();
        assertEquals(expectedText, result);
    }
}
