package seng302.State;

import java.time.LocalDate;
import java.util.ArrayList;

import seng302.Client;

/**
 * The class to handle the Client inputs, including adding,
 * setting attributes and updating the values of the client.
 * @author Dylan Carlyle, Jack Steel
 * @version sprint 1.
 * date 08/03/2018
 */

public class ClientManager {

    private ArrayList<Client> clients;
    private int uid;

    public ClientManager() {
        clients = new ArrayList<>();
        uid = 1;
    }

    public ClientManager(ArrayList<Client> clients) {
        this.clients = clients;
        uid = calculateNextId();
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
     * Get the next user ID
     * @return Next userID to be used
     */
    public int getUid() {
        return uid++;
    }

    /**
     * Set the user ID
     * @param uid Value to set the user IF
     */
    public void setUid(int uid) {
        this.uid = uid;
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

    private int calculateNextId() {
        int id = 1;
        for (Client client : clients) {
            if (client.getUid() >= id) {
                id = client.getUid() + 1;
            }
        }
        return id;
    }

}
