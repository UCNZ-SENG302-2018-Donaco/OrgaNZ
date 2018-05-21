package seng302.State;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import seng302.Client;
import seng302.TransplantRequest;

/**
 * Handles the manipulation of the clients currently stored in the system.
 */
public interface ClientManager {

    List<Client> getClients();

    void setClients(Collection<Client> clients);

    void addClient(Client client);

    void removeClient(Client client);

    /**
     * Returns the client that has the given id.
     * @param id The ID to match.
     * @return The client with that id, or null if no such client exists.
     */
    Client getClientByID(int id);

    /**
     * Checks if a client already exists with the same first name, last name, and date of birth.
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth
     * @return true if a colliding client exists in the manager, false otherwise.
     */
    boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth);

    /**
     * Returns the next unused id number for a new client.
     * @return The next free UID.
     */
    int nextUid();

    /**
     * Gets all transplant requests for all clients stored by the manager, regardless of whether or not they are
     * current.
     * @return All transplant requests.
     */
    Collection<TransplantRequest> getAllTransplantRequests();

    /**
     * Gets all transplant requests for all clients stored by the manager that are CURRENT, i.e. have not yet taken
     * place/been cancelled.
     * @return All current transplant requests.
     */
    Collection<TransplantRequest> getAllCurrentTransplantRequests();
}
