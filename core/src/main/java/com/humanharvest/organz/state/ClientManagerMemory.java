package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
import com.humanharvest.organz.views.client.TransplantRequestView;

/**
 * An in-memory implementation of {@link ClientManager} that uses a simple list to hold all clients.
 */
public class ClientManagerMemory implements ClientManager {

    private final List<Client> clients = new ArrayList<>();

    public ClientManagerMemory() {
    }

    public ClientManagerMemory(Collection<Client> clients) {
        setClients(clients);
    }

    @Override
    public void setClients(Collection<Client> clients) {
        this.clients.clear();
        for (Client client : clients) {
            addClient(client);
        }
    }

    /**
     * Add a client
     * @param client Client to be added
     */
    @Override
    public void addClient(Client client) {
        if (client.getUid() == null) {
            client.setUid(nextUid());
        }
        clients.add(client);
    }

    /**
     * Get the list of clients
     * @return ArrayList of current clients
     */
    @Override
    public List<Client> getClients() {
        return Collections.unmodifiableList(clients);
    }

    /**
     * Remove a client object
     * @param client Client to be removed
     */
    @Override
    public void removeClient(Client client) {
        clients.remove(client);
    }

    @Override
    public void applyChangesTo(Client client) {
        // Ensure that all records associated with the client have an id
        long nextId;

        nextId = client.getTransplantRequests().stream()
                .mapToLong(request -> request.getId() == null ? 0 : request.getId())
                .max().orElse(0) + 1;
        for (TransplantRequest request : client.getTransplantRequests()) {
            if (request.getId() == null) {
                request.setId(nextId);
                nextId++;
            }
        }
        nextId = client.getMedications().stream()
                .mapToLong(record -> record.getId() == null ? 0 : record.getId())
                .max().orElse(0) + 1;
        for (MedicationRecord record : client.getMedications()) {
            if (record.getId() == null) {
                record.setId(nextId);
                nextId++;
            }
        }
        nextId = client.getIllnesses().stream()
                .mapToLong(record -> record.getId() == null ? 0 : record.getId())
                .max().orElse(0) + 1;
        for (IllnessRecord record : client.getIllnesses()) {
            if (record.getId() == null) {
                record.setId(nextId);
                nextId++;
            }
        }
        nextId = client.getProcedures().stream()
                .mapToLong(record -> record.getId() == null ? 0 : record.getId())
                .max().orElse(0) + 1;
        for (ProcedureRecord record : client.getProcedures()) {
            if (record.getId() == null) {
                record.setId(nextId);
                nextId++;
            }
        }
    }

    /**
     * Checks if a user already exists with that first + last name and date of birth
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth (LocalDate)
     * @return Boolean
     */
    @Override
    public boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth) {
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
     * @return Client object or empty if none exists
     */
    @Override
    public Optional<Client> getClientByID(int id) {
        return clients.stream()
                .filter(client -> client.getUid() == id)
                .findFirst();
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
    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        return clients.stream()
                .map(Client::getTransplantRequests)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Gets all current transplant requests.
     * @return List of all current transplant requests
     */
    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return clients.stream()
                .map(Client::getTransplantRequests)
                .flatMap(Collection::stream)
                .filter(request -> request.getStatus() == TransplantRequestStatus.WAITING)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedTransplantList getAllCurrentTransplantRequests(Integer offset, Integer count,
            Set<Region> regions, Set<Organ> organs) {
        // Determine requests that match filters
        List<TransplantRequestView> matchingRequests = getClients().stream()
                .filter(client -> regions == null || regions.contains(client.getRegion()))
                .flatMap(client -> client.getTransplantRequests().stream())
                .filter(request -> organs == null || organs.contains(request.getRequestedOrgan()))
                .map(TransplantRequestView::new)
                .collect(Collectors.toList());

        // Return subset for given offset/count parameters (used for pagination)
        if (offset == null) {
            offset = 0;
        }
        if (count == null) {
            return new PaginatedTransplantList(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            matchingRequests.size()),
                    matchingRequests.size());
        } else {
            return new PaginatedTransplantList(
                    matchingRequests.subList(
                            Math.min(offset, matchingRequests.size()),
                            Math.min(offset + count, matchingRequests.size())),
                    matchingRequests.size());
        }
    }
}
