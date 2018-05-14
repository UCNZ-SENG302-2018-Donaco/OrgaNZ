package seng302.Database;


import java.util.Set;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import seng302.MedicationRecord;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


/**
 * A handler for all database requests.
 * Uses a Hibernate SessionFactory (that can be dependency-injected) for all database interactions.
 */
public class DBManager {

    private SessionFactory sessionFactory;

    /**
     * Builds the default Hibernate SessionFactory based on the Hibernate config file.
     * @return A new Hibernate SessionFactory.
     */
    private static SessionFactory buildSessionFactory() {
        return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    /**
     * Default no-args constructor that sets the manager's SessionFactory to the default one returned by
     * {@link #buildSessionFactory()}.
     */
    public DBManager() {
        this.sessionFactory = buildSessionFactory();
    }

    /**
     * Constructor that can be used to dependency-inject the manager's SessionFactory.
     * @param sessionFactory The SessionFactory for this manager to use for its database interactions.
     */
    public DBManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Saves an entity (representing a record in a table) to the database.
     * If the entity is already present in the database, then its record is updated if necessary.
     * If the entity is not yet present in the database, then a record for it is inserted into its table.
     * @param entity The entity to save. Must be of a type that is annotated with the JPA @Entity tag, and that has a
     * table within the database.
     * @throws PersistenceException If an error occurs when saving the entity to the database.
     */
    public void saveEntity(Object entity) throws PersistenceException {
        Transaction trns = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            trns = session.beginTransaction();
            session.save(entity);
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
            throw new PersistenceException("An error occurred while saving an entity: \n" + exc.getMessage());
        }
    }

    public Set<MedicationRecord> retrieveAllMedicationRecords() {
        return null;
    }
}
