package com.humanharvest.organz.state;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.RollbackException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.database.DBManager;
import org.hibernate.Transaction;

/**
 * A pure database implementation of {@link ClientManager} that uses a database to store clients, then retrieves them
 * every time a request is made (no caching).
 */
public class ClientManagerDBPure implements ClientManager {

    private final DBManager dbManager;

    public ClientManagerDBPure() {
        this.dbManager = DBManager.getInstance();
    }

    public ClientManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Client> getClients() {
        List<Client> clients = null;
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            clients = dbManager.getDBSession()
                    .createQuery("FROM Client", Client.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        return clients == null ? new ArrayList<>() : clients;
    }

    @Override
    public void setClients(Collection<Client> clients) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            session.createQuery("DELETE FROM Client").executeUpdate();

            for (Client client : clients) {
                session.save(client);
            }

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public void addClient(Client client) {
        dbManager.saveEntity(client);
    }

    @Override
    public void removeClient(Client client) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().remove(client);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public void applyChangesTo(Client client) {
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().update(client);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public Optional<Client> getClientByID(int id) {
        Transaction trns = null;
        Client client = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            client = dbManager.getDBSession().find(Client.class, id);

            trns.commit();
        } catch (RollbackException exc) {
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

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            collision = dbManager.getDBSession().createQuery("SELECT c FROM Client c "
                    + "WHERE c.firstName = :firstName "
                    + "AND c.lastName = :lastName "
                    + "AND c.dateOfBirth = :dateOfBirth", Client.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("dateOfBirth", dateOfBirth)
                    .getResultList().size() > 0;
            trns.commit();
        } catch (RollbackException exc) {
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

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = dbManager.getDBSession()
                    .createQuery("FROM TransplantRequest", TransplantRequest.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
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

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = dbManager.getDBSession()
                    .createQuery("SELECT req FROM TransplantRequest req "
                            + "WHERE req.status = "
                                    + "com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING",
                            TransplantRequest.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        return requests == null ? new ArrayList<>() : requests;
    }
}
