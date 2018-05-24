package seng302.State;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.RollbackException;

import seng302.Client;
import seng302.Database.DBManager;
import seng302.TransplantRequest;

import org.hibernate.Session;
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

        try (Session session = dbManager.getDBSession()) {
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
        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

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
        try (Session session = dbManager.getDBSession()) {
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
    public Client getClientByID(int id) {
        return dbManager.getDBSession().find(Client.class, id);
    }

    @Override
    public boolean collisionExists(String firstName, String lastName, LocalDate dateOfBirth) {
        boolean collision = false;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
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
    public int nextUid() {
        return 0;
    }

    @Override
    public Collection<TransplantRequest> getAllTransplantRequests() {
        List<TransplantRequest> requests = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
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

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            requests = dbManager.getDBSession()
                    .createQuery("SELECT req FROM TransplantRequest req "
                            + "WHERE req.status = TransplantRequestStatus.WAITING", TransplantRequest.class)
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
