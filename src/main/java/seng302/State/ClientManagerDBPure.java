package seng302.State;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import javax.persistence.RollbackException;

import seng302.Client;
import seng302.Database.DBManager;
import seng302.TransplantRequest;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
        Session session = dbManager.getDBSession();
        Transaction transaction = session.beginTransaction();
        Query<Client> query = session.createQuery("from Client", Client.class);
        List<Client> result = query.getResultList();
        transaction.commit();
        return result;


    }

    @Override
    public void setClients(Collection<Client> clients) {

    }

    @Override
    public void addClient(Client client) {

    }

    @Override
    public void removeClient(Client client) {

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
        return null;
    }

    @Override
    public Collection<TransplantRequest> getAllCurrentTransplantRequests() {
        return null;
    }
}
