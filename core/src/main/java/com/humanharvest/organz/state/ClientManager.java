package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.ClientNameSorter;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;

/**
 * Handles the manipulation of the clients currently stored in the system.
 */
public interface ClientManager {

    List<Client> getClients();

    // TODO: Change so regions isn't an enum
    default PaginatedClientList getClients(
            String q,
            Integer offset,
            Integer count,
            Integer minimumAge,
            Integer maximumAge,
            EnumSet<Region> regions,
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

    void setClients(Collection<Client> clients);

    void addClient(Client client);

    void removeClient(Client client);

    void applyChangesTo(Client client);

    /**
     * Returns the client that has the given id.
     * @param id The ID to match.
     * @return The client with that id, or empty if no such client exists.
     */
    Optional<Client> getClientByID(int id);

    /**
     * Checks if a client already exists with the same first name, last name, and date of birth.
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth
     * @return true if a colliding client exists in the manager, false otherwise.
     */
    boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth);

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

    PaginatedTransplantList getAllCurrentTransplantRequests(Integer offset, Integer count, Set<Region> regions,
            Set<Organ> organs);

    /**
     * Returns a collection of all the organs that are available to donate from dead peop[e.
     */
    Collection<DonatedOrgan> getAllOrgansToDonate();

    DonatedOrgan manuallyExpireOrgan(DonatedOrgan organ);

    /**
     * Returns a collection of all the organs that are available to donate from dead peop[e.
     * todo replace this test implementation with memory and rest versions
     */
    default PaginatedDonatedOrgansList getAllOrgansToDonate(Integer offset, Integer count) {
        System.out.println("getting organs td");
        // get the first client
        Client client = getClients().get(0);

        if (client == null) {
            System.out.println("no client found");
        } else {
            System.out.println(client.getFirstName());
        }
        DonatedOrgan organ0h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().plusSeconds(1));
        DonatedOrgan organ5m = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusMinutes(5).plusSeconds(1));
        DonatedOrgan organ1h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(1).plusSeconds(1));
        DonatedOrgan organ2h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(2).plusSeconds(1));
        DonatedOrgan organ3h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(3).plusSeconds(1));
        DonatedOrgan organ4h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(4).plusSeconds(1));
        DonatedOrgan organ5h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(5).plusSeconds(1));
        DonatedOrgan organ6h = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(6).plusSeconds(2));
        DonatedOrgan organ2a = new DonatedOrgan(Organ.CORNEA, client, LocalDateTime.now().minusDays(4).plusSeconds
                (1));
        DonatedOrgan organ2 = new DonatedOrgan(Organ.SKIN, client, LocalDateTime.now().minusYears(1));
        DonatedOrgan organ3 = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now().minusHours(5).minusMinutes(55));
                //.minusSeconds(50));
        DonatedOrgan organ4 = new DonatedOrgan(Organ.LUNG, client, LocalDateTime.now());
        List<DonatedOrgan> organsToDonate = new ArrayList<>();
        organsToDonate.add(organ0h);
        organsToDonate.add(organ5m);
        organsToDonate.add(organ3h);
        organsToDonate.add(organ2h);
        organsToDonate.add(organ1h);
        organsToDonate.add(organ4h);
        organsToDonate.add(organ5h);
        organsToDonate.add(organ6h);
        organsToDonate.add(organ2);
        organsToDonate.add(organ2a);
        organsToDonate.add(organ3);
        organsToDonate.add(organ4);
        return new PaginatedDonatedOrgansList(organsToDonate, 200);
    }


    List<HistoryItem> getAllHistoryItems();
}
