package seng302.State;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import seng302.Client;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ClientManagerTest {

    private ClientManager manager;
    private Client client;
    private Client client2;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() {
        manager = new ClientManager();
        client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(client);
        client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
    }

    @Test
    public void addClientTest() {
        manager.addClient(client2);
        assertTrue(manager.getClients().contains(client2));
    }


    @Test
    public void getClientsTest() {
        manager.addClient(client2);

        assertTrue(manager.getClients().contains(client));
        assertTrue(manager.getClients().contains(client2));
    }

    @Test
    public void removeClientTest() {
        manager.addClient(client2);
        manager.removeClient(client2);

        assertTrue(manager.getClients().contains(client));
        assertFalse(manager.getClients().contains(client2));
    }

    @Test
    public void updateClientTest() {
        client.setFirstName("New");

        assertTrue(manager.getClients().contains(client));
        assertEquals(manager.getClientByID(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        assertFalse(manager.collisionExists("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        assertFalse(manager.collisionExists("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        assertTrue(manager.collisionExists("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getClientByIDExistsTest() {
        assertTrue(manager.getClientByID(1) != null);
    }

    @Test
    public void getClientByIDDoesNotExistTest() {
        assertTrue(manager.getClientByID(2) == null);
    }


    @Test
    public void getTransplantWaitingList() {
        TransplantRequest transplantRequest = new TransplantRequest(Organ.LIVER, true);
        TransplantRequest transplantRequest2 = new TransplantRequest(Organ.LIVER, false);
        client.addTransplantRequest(transplantRequest);
        client.addTransplantRequest(transplantRequest2);

        List<TransplantRequest> waitingList = manager.getTransplantWaitingList();

        assertTrue(waitingList.contains(transplantRequest));
        assertFalse(waitingList.contains(transplantRequest2));
    }

    @Test
    public void getAllTransplantRequests() {
        TransplantRequest transplantRequest = new TransplantRequest(Organ.LIVER, true);
        TransplantRequest transplantRequest2 = new TransplantRequest(Organ.LIVER, false);
        client.addTransplantRequest(transplantRequest);
        client.addTransplantRequest(transplantRequest2);

        List<TransplantRequest> transplantRequests = manager.getAllTransplantRequests();

        assertTrue(transplantRequests.contains(transplantRequest));
        assertTrue(transplantRequests.contains(transplantRequest2));
    }

    @Test
    public void getAllCurrentTransplantRequests() {
        TransplantRequest transplantRequest = new TransplantRequest(Organ.LIVER, true);
        TransplantRequest transplantRequest2 = new TransplantRequest(Organ.LIVER, false);
        client.addTransplantRequest(transplantRequest);
        client.addTransplantRequest(transplantRequest2);

        List<TransplantRequest> transplantRequests = manager.getAllCurrentTransplantRequests();

        assertTrue(transplantRequests.contains(transplantRequest));
        assertFalse(transplantRequests.contains(transplantRequest2));
    }
}
