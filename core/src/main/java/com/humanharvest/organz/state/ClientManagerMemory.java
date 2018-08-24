package com.humanharvest.organz.state;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.ClientNameSorter;
import com.humanharvest.organz.utilities.algorithms.MatchOrganToRecipients;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;
import com.humanharvest.organz.views.client.TransplantRequestView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public final void setClients(Collection<Client> clients) {
        this.clients.clear();
        for (Client client : clients) {
            addClient(client);
        }
    }

    /**
     * Add a client
     *
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
     *
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
            Set<Gender> birthGenders,
            ClientType clientType,
            Set<Organ> donating,
            Set<Organ> requesting,
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
     *
     * @param client Client to be removed
     */
    @Override
    public void removeClient(Client client) {
        clients.remove(client);
    }

    @Override
    public void applyChangesTo(Client client) {
        // Ensure that all records associated with the client have an id

        long nextId = client.getTransplantRequests().stream()
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
     *
     * @param firstName   First name
     * @param lastName    Last name
     * @param dateOfBirth Date of birth (LocalDate)
     * @return Boolean
     */
    @Override
    public boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth) {
        for (Client client : clients) {
            if (Objects.equals(client.getFirstName(), firstName) &&
                    Objects.equals(client.getLastName(), lastName) &&
                    client.getDateOfBirth().isEqual(dateOfBirth)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a client matching that UID
     *
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
     *
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
     *
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
     *
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
        for (Client client : clients) {
            donatedOrgans.addAll(client.getDonatedOrgans());
        }
        return donatedOrgans;
    }

    /**
     * donatedOrgans,totalResults)
     *
     * @return a list of all organs available for donation
     */
    @Override
    public PaginatedDonatedOrgansList getAllOrgansToDonate(
            Integer offset,
            Integer count,
            Set<String> regionsToFilter,
            Set<Organ> organType,
            DonatedOrganSortOptionsEnum sortOption,
            Boolean reversed) {

        Comparator<DonatedOrgan> comparator;
        if (sortOption == null) {
            comparator = Comparator.comparing(DonatedOrgan::getDurationUntilExpiry,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            switch (sortOption) {
                case CLIENT:
                    comparator = Comparator.comparing(organ -> organ.getDonor().getFullName());
                    break;
                case ORGAN_TYPE:
                    comparator = Comparator.comparing(organ -> organ.getOrganType().toString());
                    break;
                case REGION_OF_DEATH:
                    comparator = Comparator.comparing(organ -> organ.getDonor().getRegionOfDeath());
                    break;
                case TIME_OF_DEATH:
                    comparator = Comparator.comparing(organ -> organ.getDonor().getDateOfDeath());
                    break;
                default:
                case TIME_UNTIL_EXPIRY:
                    comparator = Comparator.comparing(DonatedOrgan::getDurationUntilExpiry,
                            Comparator.nullsLast(Comparator.naturalOrder()));
                    break;
            }
        }

        if (reversed != null && reversed) {
            comparator = comparator.reversed();
        }

        // Get all organs for donation
        // Filter by region and organ type if the params have been set
        List<DonatedOrgan> filteredOrgans = clients.stream()
                .map(Client::getDonatedOrgans)
                .flatMap(Collection::stream)
                .filter(organ -> organ.getDurationUntilExpiry() == null || !organ.getDurationUntilExpiry().isZero())
                .filter(organ -> organ.getOverrideReason() == null)
                .filter(organ -> regionsToFilter.isEmpty()
                        || regionsToFilter.contains(organ.getDonor().getRegionOfDeath())
                        || regionsToFilter.contains("International") && organ.getDonor().getCountryOfDeath() != Country.NZ)
                .filter(organ -> organType == null || organType.isEmpty()
                        || organType.contains(organ.getOrganType()))
                .collect(Collectors.toList());

        int totalResults = filteredOrgans.size();
        if (offset == null) {
            offset = 0;
        }
        if (count == null) {
            count = Integer.MAX_VALUE;
        }

        return new PaginatedDonatedOrgansList(
                filteredOrgans.stream()
                        .sorted(comparator)
                        .skip(offset)
                        .limit(count)
                        .map(DonatedOrganView::new)
                        .collect(Collectors.toList()),
                totalResults);
    }

    /**
     * Gets potential recipients for an available organ
     *
     * @param donatedOrgan available organ to match to potential recipients
     * @return list of clients that are waiting for the available organ
     */
    @Override
    public List<Client> getOrganMatches(DonatedOrgan donatedOrgan) {
        return MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan, getAllCurrentTransplantRequests());
    }
}
