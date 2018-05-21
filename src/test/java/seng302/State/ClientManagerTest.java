package seng302.State;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import seng302.Client;
import seng302.TransplantRequest;

import seng302.Utilities.Enums.Organ;

import org.junit.Before;
import org.junit.Test;
import seng302.Utilities.Enums.RequestStatus;

public class ClientManagerTest {

    private ClientManager manager;
    private Client client1;
    private Client client2;

    @Before
    public void init() {
        manager = new ClientManager();
        client1 = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(client1);
        client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
    }

    @Test
    public void clientListConstructorTest() {
        Client client3 = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        Client client4 = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> clientList = new ArrayList<>();
        clientList.add(client3);
        clientList.add(client4);

        ClientManager manager = new ClientManager(clientList);
        ArrayList<Client> managerClientList = manager.getClients();
        assertEquals(clientList, managerClientList);
    }

    @Test
    public void addClientTest() {
        manager.addClient(client2);
        assertTrue(manager.getClients().contains(client2));
    }


    @Test
    public void getClientsTest() {
        manager.addClient(client2);

        assertTrue(manager.getClients().contains(client1));
        assertTrue(manager.getClients().contains(client2));
    }

    @Test
    public void removeClientTest() {
        manager.addClient(client2);
        manager.removeClient(client2);

        assertTrue(manager.getClients().contains(client1));
        assertFalse(manager.getClients().contains(client2));
    }

    @Test
    public void updateClientTest() {
        client1.setFirstName("New");

        assertTrue(manager.getClients().contains(client1));
        assertEquals(manager.getClientByID(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManager(clients);

        assertFalse(manager.collisionExists("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManager(clients);

        assertFalse(manager.collisionExists("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManager(clients);

        assertTrue(manager.collisionExists("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getClientByIDExistsTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManager(clients);

        assertNotNull(manager.getClientByID(1));
    }

    @Test
    public void getClientByIDDoesNotExistTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManager(clients);

        assertNull(manager.getClientByID(2));
    }

    @Test
    public void getAllTransplantRequests() {
        TransplantRequest transplantRequest = new TransplantRequest(client1, Organ.LIVER);
        TransplantRequest transplantRequest2 = new TransplantRequest(client2, Organ.HEART);
        manager.addClient(client2);
        client1.addTransplantRequest(transplantRequest);
        client2.addTransplantRequest(transplantRequest2);

        Collection<TransplantRequest> transplantRequests = manager.getAllTransplantRequests();

        assertTrue(transplantRequests.contains(transplantRequest));
        assertTrue(transplantRequests.contains(transplantRequest2));
    }

    @Test
    public void getAllCurrentTransplantRequests() {
        TransplantRequest transplantRequest = new TransplantRequest(client1, Organ.LIVER);
        TransplantRequest transplantRequest2 = new TransplantRequest(client2, Organ.HEART);
        transplantRequest2.setStatus(RequestStatus.COMPLETED);
        transplantRequest2.setResolvedDate(LocalDateTime.now());
        client1.addTransplantRequest(transplantRequest);
        client2.addTransplantRequest(transplantRequest2);

        Collection<TransplantRequest> transplantRequests = manager.getAllCurrentTransplantRequests();

        assertTrue(transplantRequests.contains(transplantRequest));
        assertFalse(transplantRequests.contains(transplantRequest2));
    }
}
