package seng302.State;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ClientManagerTest {

    private ClientManager manager;
    private Client baseClient;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() {
        manager = new ClientManager();
        baseClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
    }

    @Test
    public void addClientTest() {
        manager.addClient(baseClient);
        assertTrue(manager.getClients().contains(baseClient));
    }


    @Test
    public void getClientsTest() {
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        clients.add(client2);
        manager = new ClientManager(clients);

        assertTrue(manager.getClients().contains(baseClient));
        assertTrue(manager.getClients().contains(client2));
    }

    @Test
    public void removeClientTest() {
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        clients.add(client2);
        manager = new ClientManager(clients);

        manager.removeClient(client2);

        assertTrue(manager.getClients().contains(baseClient));
        assertFalse(manager.getClients().contains(client2));
    }

    @Test
    public void updateClientTest() {
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        clients.add(client2);
        manager = new ClientManager(clients);

        baseClient.setFirstName("New");

        assertTrue(manager.getClients().contains(baseClient));
        assertEquals(manager.getClientByID(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        manager = new ClientManager(clients);

        assertFalse(manager.collisionExists("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        manager = new ClientManager(clients);

        assertFalse(manager.collisionExists("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        manager = new ClientManager(clients);

        assertTrue(manager.collisionExists("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getClientByIDExistsTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        manager = new ClientManager(clients);

        assertNotNull(manager.getClientByID(1));
    }

    @Test
    public void getClientByIDDoesNotExistTest() {
        ArrayList<Client> clients = new ArrayList<>();
        clients.add(baseClient);
        manager = new ClientManager(clients);

        assertNull(manager.getClientByID(2));
    }
}
