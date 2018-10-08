package com.humanharvest.organz.state;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DashboardStatistics;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.algorithms.MatchOrganToRecipients;
import com.humanharvest.organz.utilities.enums.ClientSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.ClientType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.DonatedOrganView;
import com.humanharvest.organz.views.client.PaginatedClientList;
import com.humanharvest.organz.views.client.PaginatedDonatedOrgansList;
import com.humanharvest.organz.views.client.PaginatedTransplantList;

import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * A pure database implementation of {@link ClientManager} that uses a database to store clients, then retrieves them
 * every time a request is made (no caching).
 */
public class ClientManagerDBPure implements ClientManager {

    private static final Logger LOGGER = Logger.getLogger(ClientManagerDBPure.class.getName());
    private static final Pattern WHITE_SPACE = Pattern.compile("(%20|\\s)+");

    private final DBManager dbManager;

    public ClientManagerDBPure() {
        dbManager = DBManager.getInstance();
    }

    public ClientManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Client> getClients() {
        List<Client> clients;

        try (Session session = dbManager.getDBSession()) {
            session.beginTransaction();
            clients = session
                    .createQuery("FROM Client", Client.class)
                    .getResultList();
        }

        return clients == null ? new ArrayList<>() : clients;
    }

    @Override
    public void setClients(Collection<Client> clients) {
        // Clear all clients currently in the database
        clearPersistedClients();

        Transaction trns = null;
        try (Session session = dbManager.getDBSession()) {
            // Persist all the clients in the given collection
            trns = session.beginTransaction();
            for (Client client : clients) {
                if (client.getUid() == null) {
                    client.setUid(0);
                }
                session.replicate(client, ReplicationMode.OVERWRITE);
            }
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }
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

        Transaction trns = null;

        // Setup the client type filters. For this we use an EXISTS (or NOT) then a separate SELECT on
        // the respective table where uid=uid.
        // LIMIT 1 is an efficiency increase as we do not need to keep looking once we have a result (boolean true)
        String isDonor = "EXISTS (SELECT donating.Client_uid FROM Client_organsDonating AS donating "
                + "WHERE donating.Client_uid=c.uid LIMIT 1)";
        String notIsDonor = "NOT EXISTS (SELECT donating.Client_uid FROM Client_organsDonating AS donating "
                + "WHERE donating.Client_uid=c.uid LIMIT 1)";
        String isRequesting = "EXISTS (SELECT requesting.Client_uid FROM TransplantRequest AS requesting "
                + "WHERE requesting.Client_uid=c.uid LIMIT 1)";
        String notIsRequesting = "NOT EXISTS (SELECT requesting.Client_uid FROM TransplantRequest AS requesting "
                + "WHERE requesting.Client_uid=c.uid LIMIT 1)";

        //TODO: Make this use the complex sort as in ClientNameSorter
        String nameSort = "lastName";

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            Map<String, Object> params = new HashMap<>();
            StringBuilder joinBuilder = new StringBuilder();
            StringJoiner whereJoiner = new StringJoiner(" AND ");

            //Setup minimum age filter
            if (minimumAge != null) {
                //Use the TIMESTAMPDIFF (MySQL only) function to calculate age
                whereJoiner.add(
                        "CASE WHEN dateOfDeath IS NULL THEN TIMESTAMPDIFF(YEAR, c.dateOfBirth, NOW()) >= :minimumAge "
                                + "ELSE TIMESTAMPDIFF(YEAR, c.dateOfBirth, c.dateOfDeath) >= :minimumAge END");
                params.put("minimumAge", minimumAge);
            }

            //Setup maximum age filter
            if (maximumAge != null) {
                //Use the TIMESTAMPDIFF (MySQL only) function to calculate age
                whereJoiner.add(
                        "CASE WHEN dateOfDeath IS NULL THEN TIMESTAMPDIFF(YEAR, c.dateOfBirth, NOW()) <= :maximumAge "
                                + "ELSE TIMESTAMPDIFF(YEAR, c.dateOfBirth, c.dateOfDeath) <= :maximumAge END");
                params.put("maximumAge", maximumAge);
            }

            // Setup region filter.
            if (regions != null && !regions.isEmpty()) {
                whereJoiner.add("c.region IN (:regions)");
                params.put("regions", regions);
            }

            // Setup birth gender filter.
            if (birthGenders != null && !birthGenders.isEmpty()) {
                whereJoiner.add("c.gender IN (:genders)");
                // Map the genders to strings as they are stored that way in the DB
                params.put("genders", birthGenders.stream().map(Gender::name).collect(Collectors.toList()));
            }

            // Setup donating filter.
            // We use an INNER JOIN and therefor select only clients where they have an entry in
            // the Client_organsDonating table that matches one of the given organs
            if (donating != null && !donating.isEmpty()) {
                String joinQuery = " INNER JOIN (SELECT donating.Client_uid FROM Client_organsDonating AS donating "
                        + "WHERE donating.organsDonating IN (:donating) "
                        + "GROUP BY donating.Client_uid) donating ON c.uid=donating.Client_uid ";

                joinBuilder.append(joinQuery);
                // Map the organs to strings as they are stored that way in the DB
                params.put("donating", donating.stream().map(Organ::name).collect(Collectors.toList()));
            }

            // Setup requesting filter.
            // We use an INNER JOIN and therefor select only clients where they have an entry in
            // the TransplantRequest table that matches one of the given organs and is status=WAITING
            if (requesting != null && !requesting.isEmpty()) {
                String joinQuery = " INNER JOIN (SELECT requesting.Client_uid FROM TransplantRequest AS requesting "
                        + "WHERE requesting.status='WAITING' AND requesting.requestedOrgan IN (:requesting) "
                        + "GROUP BY requesting.Client_uid) requesting ON c.uid=requesting.Client_uid ";

                joinBuilder.append(joinQuery);
                // Map the organs to strings as they are stored that way in the DB
                params.put("requesting", requesting.stream().map(Organ::name).collect(Collectors.toList()));
            }

            // Setup the client type filter. For this we use an EXISTS (or NOT) then a separate SELECT on
            // the respective table where uid=uid.
            // LIMIT 1 is an efficiency increase as we do not need to keep looking once we have a result (boolean true)
            if (clientType != null) {
                switch (clientType) {
                    case BOTH:
                        whereJoiner.add(isDonor);
                        whereJoiner.add(isRequesting);
                        break;

                    case NEITHER:
                        whereJoiner.add(notIsDonor);
                        whereJoiner.add(notIsRequesting);
                        break;

                    case ONLY_DONOR:
                        whereJoiner.add(isDonor);
                        whereJoiner.add(notIsRequesting);
                        break;

                    case ONLY_RECEIVER:
                        whereJoiner.add(notIsDonor);
                        whereJoiner.add(isRequesting);
                        break;
                    case ANY:
                    default:
                        // Do nothing because we do not want to apply any filters in this case
                }
            }

            //Setup the name filter. For this we make a series of OR checks on the names, if any is true it's true.
            //Checks any portion of any name
            if (q != null && !q.isEmpty()) {
                String[] qParts = WHITE_SPACE.split(q);
                StringJoiner qAndJoiner = new StringJoiner(" AND ");

                //For every substring, make sure that it matches any of the names
                int i = 0;
                for (String qPart : qParts) {
                    StringJoiner qOrJoiner = new StringJoiner(" OR ");
                    qOrJoiner.add("UPPER(c.firstName) LIKE UPPER(:q" + i + ")");
                    qOrJoiner.add("UPPER(c.middleName) LIKE UPPER(:q" + i + ")");
                    qOrJoiner.add("UPPER(c.preferredName) LIKE UPPER(:q" + i + ")");
                    qOrJoiner.add("UPPER(c.lastName) LIKE UPPER(:q" + i + ")");
                    params.put("q" + i, "%" + qPart + "%");
                    qAndJoiner.add("(" + qOrJoiner + ")");
                    i++;
                }
                whereJoiner.add("(" + qAndJoiner + ")");
            }

            //Set offset to zero if not given
            if (offset == null || offset < 0) {
                offset = 0;
            }
            //Set count to all if not given
            if (count == null || count < 0) {
                count = Integer.MAX_VALUE;
            }

            //Setup the sort order for the given sort option. Default to NAME if none is given
            if (sortOption == null) {
                sortOption = ClientSortOptionsEnum.NAME;
            }
            String sort;
            String dir;

            switch (sortOption) {
                case ID:
                    sort = "uid";
                    break;
                case AGE:
                    sort = "CASE WHEN dateOfDeath IS NULL THEN DATEDIFF(dateOfBirth, NOW()) "
                            + "ELSE DATEDIFF(dateOfBirth, dateOfDeath) END";
                    break;
                case DONOR:
                    sort = isDonor;
                    break;
                case RECEIVER:
                    sort = isRequesting;
                    break;
                case REGION:
                    sort = "region";
                    break;
                case BIRTH_GENDER:
                    sort = "gender";
                    break;
                case NAME:
                default:
                    sort = nameSort;
            }
            if (isReversed != null && isReversed) {
                dir = "DESC";
            } else {
                dir = "ASC";
            }

            // Create the final strings, in the basic format
            // START_TEXT + JOINS + WHERES + ORDER BY + LIMIT + OFFSET
            // Only add the WHERE if there are some where checks.
            // We also do a second query for count using count(*) and no LIMIT, OFFSET

            if (whereJoiner.length() != 0) {
                joinBuilder.append(" WHERE ");
            }

            // Quite a complex string build, but all defined as above, just simple string combinations
            String queryString = "SELECT c.* FROM Client c " + joinBuilder + whereJoiner
                    + " ORDER BY " + sort + " " + dir + ", " + nameSort + " ASC LIMIT :limit OFFSET :offset";
            String countString = "SELECT count(*) FROM Client c " + joinBuilder + whereJoiner;

            Query<?> countQuery = session.createNativeQuery(countString);
            Query<Client> mainQuery = session.createNativeQuery(queryString, Client.class);

            // Go through the params and set the values.
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                mainQuery.setParameter(entry.getKey(), entry.getValue());
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }

            // Set the limit and offset for the main query
            mainQuery.setParameter("limit", count);
            mainQuery.setParameter("offset", offset);

            // Execute the queries
            int totalCount = Integer.parseInt(countQuery.uniqueResult().toString());
            List<Client> clients = mainQuery.getResultList();

            return new PaginatedClientList(clients, totalCount);

        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
            return null;
        }
    }

    private void clearPersistedClients() {
        try (Connection connection = dbManager.getStandardSqlConnection()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM Client_organsDonating");
                stmt.executeUpdate("DELETE FROM Client_HistoryItem");
                stmt.executeUpdate("DELETE FROM DonatedOrgan");
                stmt.executeUpdate("DELETE FROM IllnessRecord");
                stmt.executeUpdate("DELETE FROM MedicationRecord");
                stmt.executeUpdate("DELETE FROM ProcedureRecord_affectedOrgans");
                stmt.executeUpdate("DELETE FROM ProcedureRecord");
                stmt.executeUpdate("DELETE FROM TransplantRequest");
                stmt.executeUpdate("DELETE FROM Client");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void addClient(Client client) {
        dbManager.saveEntity(client);
    }

    @Override
    public void removeClient(Client client) {
        Transaction trns = null;
        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            session.remove(client);

            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

    @Override
    public void applyChangesTo(Client client) {
        applyChangesToObject(client);
    }

    @Override
    public void applyChangesTo(DonatedOrgan organ) {
        applyChangesToObject(organ);
    }

    @Override
    public void applyChangesTo(TransplantRequest request) {
        applyChangesToObject(request);
    }

    private void applyChangesToObject(Object object) {
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            try {
                session.update(object);
                trns.commit();
            } catch (OptimisticLockException exc) {
                // TODO fix this hack
                try (Session otherSession = dbManager.getDBSession()) {
                    trns = otherSession.beginTransaction();
                    otherSession.replicate(object, ReplicationMode.OVERWRITE);
                    trns.commit();
                }
            }

        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public Optional<Client> getClientByID(int id) {
        Transaction trns = null;
        Client client = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            client = session.find(Client.class, id);

            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return Optional.ofNullable(client);
    }

    @Override
    public boolean doesClientExist(String firstName, String lastName, LocalDate dateOfBirth) {
        boolean collision = false;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            collision = !session.createQuery("SELECT c FROM Client c "
                    + "WHERE c.firstName = :firstName "
                    + "AND c.lastName = :lastName "
                    + "AND c.dateOfBirth = :dateOfBirth", Client.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("dateOfBirth", dateOfBirth)
                    .getResultList().isEmpty();
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return collision;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        List<TransplantRequest> requests = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = session
                    .createQuery("FROM TransplantRequest", TransplantRequest.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return requests == null ? new ArrayList<>() : requests;
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        List<TransplantRequest> requests = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = session
                    .createQuery("SELECT req FROM TransplantRequest req "
                                    + "WHERE req.status = "
                                    + "com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING",
                            TransplantRequest.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return requests == null ? new ArrayList<>() : requests;
    }

    @Override
    public PaginatedTransplantList getAllCurrentTransplantRequests(Integer offset, Integer count,
            Set<String> regions, Set<Organ> organs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<HistoryItem> getAllHistoryItems() {
        List<HistoryItem> requests = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = session
                    .createQuery("SELECT item FROM HistoryItem item", HistoryItem.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return requests == null ? new ArrayList<>() : requests;
    }

    @Override
    public DashboardStatistics getStatistics() {

        try (Connection session = dbManager.getStandardSqlConnection()) {

            try (Statement stmt = session.createStatement()) {

                // Gets statistics for counts of clients, organs, and requests from the DB
                final String QUERY = "SELECT (SELECT count(*)"
                        + "        FROM Client) AS clientCount,\n"
                        + "       (SELECT count(*)\n"
                        + "        FROM Client\n"
                        + "        WHERE Client.isDonor = 1 and Client.isReceiver = 0\n"
                        + "       ) AS donorCount,\n"
                        + "        \n"
                        + "       (SELECT count(*)\n"
                        + "        FROM Client\n"
                        + "        WHERE Client.isDonor = 0 and Client.isReceiver = 1\n"
                        + "       ) AS receiverCount,\n"
                        + "        \n"
                        + "       (SELECT count(*)\n"
                        + "        FROM Client \n"
                        + "        WHERE Client.isDonor = 1 and Client.isReceiver = 1\n"
                        + "       ) AS donorReceiverCount,\n"
                        + "       \n"
                        + "       (SELECT count(*)\n"
                        + "        FROM DonatedOrgan\n"
                        + "        WHERE DonatedOrgan.available = 1\n"
                        + "       ) AS organCount,\n"
                        + "       \n"
                        + "       (SELECT count(*)\n"
                        + "        FROM TransplantRequest\n"
                        + "        WHERE TransplantRequest.status = \"WAITING\"\n"
                        + "       ) AS requestCount\n"
                        + "        \n"
                        + "FROM dual";

                try (ResultSet result = stmt.executeQuery(QUERY)) {
                    result.next();

                    return new DashboardStatistics(result.getInt("clientCount"),
                            result.getInt("donorCount"), result.getInt("receiverCount"),
                            result.getInt("donorReceiverCount"), result.getInt("organCount"),
                            result.getInt("requestCount"));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return null;
    }

    /**
     * @return a list of all organs available for donation
     */
    @Override
    public Collection<DonatedOrgan> getAllOrgansToDonate() {
        List<DonatedOrgan> requests = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = session
                    .createQuery("FROM DonatedOrgan", DonatedOrgan.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        return requests == null ? new ArrayList<>() : requests;
    }

    /**
     * A paginated list of all organs that are available for donation
     * @param offset How many results to skip (default: 0)
     * @param count How many results to return (default: all)
     * @param regionsToFilter Only return organs withing the given regions (default: all)
     * @param organType Only return organs of the given types (default: all)
     * @param sortOption Sort by the given option (default: time until expiry)
     * @param reversed If the results should be reversed (default: false)
     * @return A paginated list of donated organs matching the given filters
     */
    @Override
    public PaginatedDonatedOrgansList getAllOrgansToDonate(Integer offset, Integer count, Set<String> regionsToFilter,
            Set<Organ> organType, DonatedOrganSortOptionsEnum sortOption,
            Boolean reversed) {

        // TODO implement using Hibernate queries instead of in-memory filtering/sorting

        Comparator<DonatedOrgan> comparator = DonatedOrgan.getComparator(sortOption);

        if (reversed != null && reversed) {
            comparator = comparator.reversed();
        }

        // Get all organs for donation
        // Filter by region and organ type if the params have been set
        List<DonatedOrgan> filteredOrgans = getAllOrgansToDonate().stream()
                .filter(organ -> organ.getDurationUntilExpiry() == null || !organ.getDurationUntilExpiry().isZero())
                .filter(organ -> organ.getOverrideReason() == null)
                .filter(DonatedOrgan::isAvailable)
                .filter(organ -> regionsToFilter.isEmpty()
                        || regionsToFilter.contains(organ.getDonor().getRegionOfDeath())
                        || (regionsToFilter.contains("International")
                        && organ.getDonor().getCountryOfDeath() != Country.NZ))
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
     * @param donatedOrgan Available organ to find potential recipients for
     * @return List of clients who may receive the donated organ
     */
    @Override
    public List<Client> getOrganMatches(DonatedOrgan donatedOrgan) {

        Collection<TransplantRequest> transplantRequests = getAllTransplantRequests();
        return MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan, transplantRequests);
    }

    /**
     * @param donatedOrgan available organ to find potential matches for
     * @return list of TransplantRequests that will match the given organ
     */
    @Override
    public List<TransplantRequest> getMatchingOrganTransplants(DonatedOrgan donatedOrgan) {

        Collection<TransplantRequest> transplantRequests = getAllTransplantRequests();
        return MatchOrganToRecipients.getListOfPotentialTransplants(donatedOrgan, transplantRequests);
    }

    /**
     * @param donatedOrgan available organ to find potential matches for
     * @return The matching TransplantRecord for the given organ
     */
    @Override
    public TransplantRecord getMatchingOrganTransplantRecord(DonatedOrgan donatedOrgan) {
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            TransplantRecord record = session
                    .createQuery("SELECT req FROM TransplantRecord req WHERE req.organ = :organ",
                            TransplantRecord.class)
                    .setParameter("organ", donatedOrgan)
                    .uniqueResult();
            trns.commit();
            return record;
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
            return null;
        }
    }

    /**
     * Determines whether a donor is deceased and has chosen to donate organs that are currently available (not expired)
     * @param client client to determine viability of as an organ donor
     * @return boolean of whether the given client is viable as an organ donor
     */
    private static boolean isViableDonor(Client client) {
        if (client.isDead()) {
            for (DonatedOrgan organ : client.getDonatedOrgans()) {
                if (!organ.hasExpired() && organ.getOverrideReason() == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Fetches viable deceased donors from the database
     * @return list of viable deceased donors
     */
    @Override
    public List<Client> getViableDeceasedDonors() {

        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            String queryString = "SELECT c.* FROM Client c \n"
                    + "WHERE EXISTS (SELECT donating.donor_uid \n"
                    + "              FROM DonatedOrgan AS donating\n"
                    + "              WHERE donating.donor_uid=c.uid "
                    + "              AND donating.overrideReason IS NULL"
                    + "              AND donating.available = TRUE"
                    + "              LIMIT 1)\n"
                    + "ORDER BY c.dateOfDeath DESC";

            Query<Client> query = session.createNativeQuery(queryString, Client.class);

            List<Client> clients = new ArrayList<>();
            for (Client client : query.getResultList()) {
                if (isViableDonor(client)) {
                    clients.add(client);
                }
            }

            return clients;

        }  catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
            return null;
        }
    }
}
