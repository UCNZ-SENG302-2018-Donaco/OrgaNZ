package seng302.State;

import static seng302.TransplantRequest.RequestStatus.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import seng302.Client;
import seng302.TransplantRequest;

/**
 * Handles the manipulation of the clients currently stored in the system.
 */

public class ClientManager {

    private ArrayList<Client> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public ClientManager(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    /**
     * Add a client
     * @param client Client to be added
     */
    public void addClient(Client client) {
        clients.add(client);
    }

    /**
     * Get the list of clients
     * @return ArrayList of current clients
     */
    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * Remove a client object
     * @param client Client to be removed
     */
    public void removeClient(Client client) {
        clients.remove(client);
    }

    /**
     * Checks if a user already exists with that first + last name and date of birth
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth (LocalDate)
     * @return Boolean
     */
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        for (Client client : clients) {
            if (client.getFirstName().equals(firstName) &&
                    client.getLastName().equals(lastName) &&
                    client.getDateOfBirth().isEqual(dateOfBirth)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a client matching that UID
     * @param id To be matched
     * @return Client object or null if none exists
     */
    public Client getClientByID(int id) {
        return clients.stream()
                .filter(d -> d.getUid() == id).findFirst().orElse(null);
    }

    /**
     * Returns the next unused id number for a new client.
     * @return The next free UID.
     */
    public int nextUid() {
        OptionalInt max = clients.stream()
                .mapToInt(Client::getUid)
                .max();

        if (max.isPresent()) {
            return max.getAsInt() + 1;
        } else {
            return 1;
        }
    }

    /**
     * Gets all transplant requests, regardless of whether or not they are current
     * @return List of all transplant requests
     */
    public List<TransplantRequest> getAllTransplantRequests() {
        return clients.stream()
                .map(Client::getTransplantRequests)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Gets all current transplant requests.
     * @return List of all current transplant requests
     */
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return clients.stream()
                .map(Client::getTransplantRequests)
                .flatMap(Collection::stream)
                .filter(request -> request.getStatus() == WAITING)
                .collect(Collectors.toList());
    }
}
