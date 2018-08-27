package com.humanharvest.organz.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * A handler for all database requests.
 * Uses a Hibernate SessionFactory (that can be dependency-injected) for all database interactions.
 */
public class DBManager {

    private static DBManager dbManager;

    private SessionFactory sessionFactory;

    /**
     * Default no-args constructor that sets the manager's SessionFactory to the default one returned by
     * {@link #buildSessionFactory()}.
     */
    private DBManager() {
        this.sessionFactory = buildSessionFactory();
    }

    /**
     * Constructor that can be used to dependency-inject the manager's SessionFactory.
     *
     * @param sessionFactory The SessionFactory for this manager to use for its database interactions.
     */
    public DBManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Builds the default Hibernate SessionFactory based on the Hibernate config file.
     *
     * @return A new Hibernate SessionFactory.
     */
    private static SessionFactory buildSessionFactory() {
        return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
    }

    public static DBManager getInstance() {
        if (dbManager == null) {
            dbManager = new DBManager();
        }
        return dbManager;
    }

    /**
     * Returns a standard JDBC SQL Connection
     *
     * @return The connection object
     * @throws SQLException Thrown if there are any issues connecting to the database
     */
    public Connection getStandardSqlConnection() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
        dataSource.setURL(cfg.getProperty("hibernate.connection.url"));
        dataSource.setUser(cfg.getProperty("hibernate.connection.username"));
        dataSource.setPassword(cfg.getProperty("hibernate.connection.password"));
        return dataSource.getConnection();
    }

    /**
     * Returns a new DB session that can be used for executing database requests. IMPORTANT: This should only by used
     * by classes that are inherently coupled to using a database, e.g. ClientManagerDBPure. You should always avoid
     * using this if there is any other alternative (e.g. using methods from a ClientManager instead).
     *
     * @return A new DB session.
     */
    public Session getDBSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Saves an entity (representing a record in a table) to the database.
     * If the entity is already present in the database, then its record is updated if necessary.
     * If the entity is not yet present in the database, then a record for it is inserted into its table.
     *
     * @param entity The entity to save. Must be of a type that is annotated with the JPA @Entity tag, and that has a
     * table within the database.
     * @throws PersistenceException If an error occurs when saving the entity to the database.
     */
    public void saveEntity(Object entity) throws PersistenceException {
        Transaction trns = null;
        try (Session session = getDBSession()) {
            trns = session.beginTransaction();
            session.save(entity);
            trns.commit();
        } catch (RollbackException e) {
            if (trns != null) {
                trns.rollback();
            }
            throw new PersistenceException("An error occurred while saving an entity: \n" + e.getMessage(), e);
        }
    }
}
