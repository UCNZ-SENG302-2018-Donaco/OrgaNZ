package com.humanharvest.organz.state;

import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import java.time.LocalDate;
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
import com.humanharvest.organz.utilities.enums.*;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;

import java.time.LocalDate;
import java.util.*;

/**
 * Handles the manipulation of the clients currently stored in the system.
 */
public interface ClientManager {

    List<Client> getClients();

    // TODO: Change so regions isn't an enum
    PaginatedClientList getClients(
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
            Boolean isReversed);

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

    Collection<DonatedOrgan> getAllOrgansToDonate();

    /**
     * Returns a collection of all the organs that are available to donate from dead peop[e.
     */
    Collection<DonatedOrgan> getAllOrgansToDonate(EnumSet<Region> regions, EnumSet<Organ> organType);
//    default PaginatedDonatedOrgansList getAllOrgansToDonate(EnumSet<Region> regions, EnumSet<Organ> organType) {
//
//        Stream<DonatedOrgan> stream = getAllOrgansToDonate().stream();
//
//
//        List<DonatedOrgan> filteredOrgans = stream
//            .filter(regions == null ? c -> true : organ -> regions.isEmpty() ||
//                regions.contains(organ.getDonor().getRegion()))
//            .collect(Collectors.toList());
//
//        int results = filteredOrgans.size();
//
//        return new PaginatedDonatedOrgansList(filteredOrgans,results);
//
//    };
    /** Used by clinician to manually expire an organ
     * @param organ Organ that clinician wishes to expire
     * @return Returns expired organ.
     */
    DonatedOrgan manuallyExpireOrgan(DonatedOrgan organ);

    List<HistoryItem> getAllHistoryItems();
}
