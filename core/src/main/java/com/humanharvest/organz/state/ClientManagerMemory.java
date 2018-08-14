package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.ClientNameSorter;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
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

    @Override
    public PaginatedClientList getClients(
            String q,
            Integer offset,
            Integer count,
            Integer minimumAge,
            Integer maximumAge,
            Set<String> regions,
            EnumSet<Gender> birthGenders,
            ClientType clientType,
            EnumSet<Organ> donating,
            EnumSet<Organ> requesting,
            ClientSortOptionsEnum sortOption,
            Boolean isReversed) {

        Stream<Client> stream = getClients().stream();

        if (offset == null) {
            offset = 0;
        }
        if (count == null) {
            count = Integer.MAX_VALUE;
        }

        //Setup the primarySorter for the given sort option. Default to NAME if none is given
        if (sortOption == null) {
            sortOption = ClientSortOptionsEnum.NAME;
        }
        Comparator<Client> primarySorter;
        switch (sortOption) {
            case ID:
                primarySorter = Comparator.comparing(Client::getUid, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case AGE:
                primarySorter = Comparator.comparing(Client::getAge, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case DONOR:
                primarySorter = Comparator.comparing(Client::isDonor, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case RECEIVER:
                primarySorter = Comparator
                        .comparing(Client::isReceiver, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case REGION:
                primarySorter = Comparator
                        .comparing(Client::getRegion, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case BIRTH_GENDER:
                primarySorter = Comparator
                        .comparing(Client::getGender, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case NAME:
            default:
                primarySorter = new ClientNameSorter(q);
        }

        //Setup a second comparison
        Comparator<Client> dualSorter = primarySorter.thenComparing(new ClientNameSorter(q));

        //If the sort should be reversed
        if (isReversed != null && isReversed) {
            dualSorter = dualSorter.reversed();
        }

        List<Client> filteredClients = stream
                .filter(q == null ? c -> true : client -> client.nameContains(q))

                .filter(minimumAge == null ? c -> true : client -> client.getAge() >= minimumAge)

                .filter(maximumAge == null ? c -> true : client -> client.getAge() <= maximumAge)

                .filter(regions == null ? c -> true : client -> regions.isEmpty() ||
                        regions.contains(client.getRegion()))

                .filter(birthGenders == null ? c -> true : client -> birthGenders.isEmpty() ||
                        birthGenders.contains(client.getGender()))

                .filter(clientType == null ? c -> true : client -> client.isOfType(clientType))

                .filter(donating == null ? c -> true : client -> donating.isEmpty() ||
                        donating.stream().anyMatch(organ -> client.getCurrentlyDonatedOrgans().contains(organ)))

                .filter(requesting == null ? c -> true : client -> requesting.isEmpty() ||
                        requesting.stream().anyMatch(organ -> client.getCurrentlyRequestedOrgans().contains(organ)))

                .collect(Collectors.toList());

        int totalResults = filteredClients.size();

        List<Client> paginatedClients = filteredClients.stream()

                .sorted(dualSorter)

                .skip(offset)

                .limit(count)

                .collect(Collectors.toList());

        return new PaginatedClientList(paginatedClients, totalResults);
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
            Set<String> regions, Set<Organ> organs) {
        // Determine requests that match filters
        List<TransplantRequestView> matchingRequests = getClients().stream()
                .filter(client -> regions == null || regions.isEmpty() || regions.contains(client.getRegion()))
                .flatMap(client -> client.getTransplantRequests().stream())
                .filter(request -> organs == null || organs.isEmpty() || organs.contains(request.getRequestedOrgan()))
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

    @Override
    public List<HistoryItem> getAllHistoryItems() {
        return clients.stream()
                .flatMap(client -> client.getChangesHistory().stream())
                .collect(Collectors.toList());
    }

    /**
     * @return a list of all organs available for donation
     */
    @Override
    public Collection<DonatedOrgan> getAllOrgansToDonate() {
        Collection<DonatedOrgan> donatedOrgans = new ArrayList<>();
        for (Client client: clients) {
            donatedOrgans.addAll(client.getDonatedOrgans());
        }
        return donatedOrgans;
    }

    /**donatedOrgans,totalResults)
     * @return a list of all organs available for donation
     */
    @Override
    public PaginatedDonatedOrgansList getAllOrgansToDonate(Integer offset, Integer count,Set<String> regions,
            EnumSet<Organ> organType) {

        List<DonatedOrganView> donatedOrgans = new ArrayList<>();

        for (Client client: clients) {
            for (DonatedOrgan organ: client.getDonatedOrgans()) {
                donatedOrgans.add(new DonatedOrganView(organ));
            }
        }
        Stream<DonatedOrganView> stream = donatedOrgans.stream();
        donatedOrgans = stream

                .filter(regions == null ? o -> true : organ -> regions.isEmpty() ||
                        regions.contains(organ.getDonatedOrgan().getDonor().getRegion()))

                .filter(organType == null ? o -> true : organ -> organType.isEmpty() ||
                        organType.contains(organ.getDonatedOrgan().getOrganType()))

                .collect(Collectors.toList());


        return new PaginatedDonatedOrgansList(donatedOrgans.subList(
                Math.min(offset, donatedOrgans.size()),
                Math.min(offset + count, donatedOrgans.size())),
                donatedOrgans.size());

    }
}
