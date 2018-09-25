package com.humanharvest.organz.state;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;

/**
 * Handles the manipulation of the clients currently stored in the system.
 */
public interface ClientManager {

    List<Client> getClients();

    void setClients(Collection<Client> clients);

    PaginatedClientList getClients(
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
            Boolean isReversed);

    void addClient(Client client);

    void removeClient(Client client);

    void applyChangesTo(Client client);

    void applyChangesTo(DonatedOrgan organ);

    void applyChangesTo(TransplantRequest request);

    /**
     * Returns the client that has the given id.
     *
     * @param id The ID to match.
     * @return The client with that id, or empty if no such client exists.
     */
    Optional<Client> getClientByID(int id);

    /**
     * Checks if a client already exists with the same first name, last name, and date of birth.
     *
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth
     * @return true if a colliding client exists in the manager, false otherwise.
     */
    boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth);

    /**
     * Gets all transplant requests for all clients stored by the manager, regardless of whether or not they are
     * current.
     *
     * @return All transplant requests.
     */
    Collection<TransplantRequest> getAllTransplantRequests();

    /**
     * Gets all transplant requests for all clients stored by the manager that are CURRENT, i.e. have not yet taken
     * place/been cancelled.
     *
     * @return All current transplant requests.
     */
    Collection<TransplantRequest> getAllCurrentTransplantRequests();

    PaginatedTransplantList getAllCurrentTransplantRequests(Integer offset, Integer count, Set<String> regions,
            Set<Organ> organs);

    Collection<DonatedOrgan> getAllOrgansToDonate();

    /**
     * Returns a collection of all the organs that are available to donate from dead peop[e.
     */
    PaginatedDonatedOrgansList getAllOrgansToDonate(Integer offset, Integer count, Set<String> regions, Set<Organ>
            organType, DonatedOrganSortOptionsEnum sortOption, Boolean reversed);

    /**
     * Gets a list of potential recipients of the given donated organ
     * @param donatedOrgan available organ to find potential matches for
     * @return list of clients who are a potential match for the donated organ
     */
    List<Client> getOrganMatches(DonatedOrgan donatedOrgan);

    /**
     * Gets all deceased donors that are viable (have non expired organs)
     * @return list of deceased donors viable to donate organs
     */
    List<Client> getViableDeceasedDonors();

    List<HistoryItem> getAllHistoryItems();

    DashboardStatistics getStatistics();
}
