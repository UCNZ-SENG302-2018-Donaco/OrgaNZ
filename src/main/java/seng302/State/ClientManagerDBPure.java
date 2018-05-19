package seng302.State;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import seng302.Client;
import seng302.Database.DBManager;
import seng302.TransplantRequest;

/**
 * A pure database implementation of {@link ClientManager} that uses a database to store clients, then retrieves them
 * every time a request is made (no caching).
 */
public class ClientManagerDBPure implements ClientManager {

    private final DBManager dbManager;

    public ClientManagerDBPure() {
        this.dbManager = DBManager.getInstance();
    }

    public ClientManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Client> getClients() {

    }

    @Override
    public void setClients(Collection<Client> clients) {

    }

    @Override
    public void addClient(Client client) {

    }

    @Override
    public void removeClient(Client client) {

    }

    @Override
    public Client getClientByID(int id) {
        return null;
    }

    @Override
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        return false;
    }

    @Override
    public int nextUid() {
        return 0;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        return null;
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return null;
    }
}
