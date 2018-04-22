package seng302.State;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ClientManagerTest {

    private ClientManager manager;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() {
        manager = new ClientManager();
    }

    @Test
    public void addClientTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        manager.addClient(client);
        assertTrue(manager.getPeople().contains(client));
    }


    @Test
    public void getClientsTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        people.add(client2);
        manager = new ClientManager(people);

        assertTrue(manager.getPeople().contains(client));
        assertTrue(manager.getPeople().contains(client2));
    }

    @Test
    public void removeClientTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        people.add(client2);
        manager = new ClientManager(people);

        manager.removeClient(client2);

        assertTrue(manager.getPeople().contains(client));
        assertFalse(manager.getPeople().contains(client2));
    }

    @Test
    public void updateClientTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        Client client2 = new Client("FirstTwo", null, "LastTwo", LocalDate.of(1970, 1, 1), 2);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        people.add(client2);
        manager = new ClientManager(people);

        client.setFirstName("New");

        assertTrue(manager.getPeople().contains(client));
        assertEquals(manager.getClientByID(1).getFirstName(), "New");
    }


    @Test
    public void collisionExsistsNoCollisionNameTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        manager = new ClientManager(people);

        assertFalse(manager.collisionExists("Not", "Same", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void collisionExsistsNoCollisionDateTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        manager = new ClientManager(people);

        assertFalse(manager.collisionExists("First", "Last", LocalDate.of(2018, 12, 12)));
    }

    @Test
    public void collisionExsistsValidCollisionTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        manager = new ClientManager(people);

        assertTrue(manager.collisionExists("First", "Last", LocalDate.of(1970, 1, 1)));
    }

    @Test
    public void getClientByIDExistsTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        manager = new ClientManager(people);

        assertTrue(manager.getClientByID(1) != null);
    }

    @Test
    public void getClientByIDDoesNotExistTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        ArrayList<Client> people = new ArrayList<>();
        people.add(client);
        manager = new ClientManager(people);

        assertTrue(manager.getClientByID(2) == null);
    }
}
