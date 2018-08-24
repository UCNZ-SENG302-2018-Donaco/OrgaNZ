package com.humanharvest.organz.state;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ClientManagerMemoryTest extends BaseTest {

    private ClientManager manager;
    private Client client1;
    private Client client2;

    @Before
    public void init() {
        manager = new ClientManagerMemory();
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

        ClientManager manager = new ClientManagerMemory(clientList);
        List<Client> managerClientList = manager.getClients();
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
        assertEquals("New", manager.getClientByID(1).orElseThrow(RuntimeException::new).getFirstName());
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManagerMemory(clients);

        assertFalse(manager.doesClientExist("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManagerMemory(clients);

        assertFalse(manager.doesClientExist("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManagerMemory(clients);

        assertTrue(manager.doesClientExist("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getClientByIDExistsTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManagerMemory(clients);

        assertNotNull(manager.getClientByID(1));
    }

    @Test
    public void getClientByIDDoesNotExistTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(client1);
        manager = new ClientManagerMemory(clients);

        assertFalse(manager.getClientByID(2).isPresent());
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
        transplantRequest2.setStatus(TransplantRequestStatus.COMPLETED);
        transplantRequest2.setResolvedDate(LocalDateTime.now());
        client1.addTransplantRequest(transplantRequest);
        client2.addTransplantRequest(transplantRequest2);

        Collection<TransplantRequest> transplantRequests = manager.getAllCurrentTransplantRequests();

        assertTrue(transplantRequests.contains(transplantRequest));
        assertFalse(transplantRequests.contains(transplantRequest2));
    }
}
