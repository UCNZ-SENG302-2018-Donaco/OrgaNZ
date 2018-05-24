package seng302.State;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import seng302.Administrator;
import seng302.Database.DBManager;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class AdministratorManagerDBPure implements AdministratorManager {

    private final DBManager dbManager;
    private Administrator defaultAdministrator = new Administrator("admin", "");

    public AdministratorManagerDBPure() {
        this.dbManager = DBManager.getInstance();
        tryInsertDefault();
    }

    public AdministratorManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
        tryInsertDefault();
    }

    private void tryInsertDefault() {
        try {
            dbManager.saveEntity(defaultAdministrator);
        } catch (PersistenceException ignored) {
        }
    }

    @Override
    public void addAdministrator(Administrator administrator) {
        dbManager.saveEntity(administrator);
    }

    @Override
    public List<Administrator> getAdministrators() {
        List<Administrator> clinicians = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            clinicians = dbManager.getDBSession()
                    .createQuery("FROM Administrator ", Administrator.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        return clinicians == null ? new ArrayList<>() : clinicians;
    }

    @Override
    public void removeAdministrator(Administrator administrator) {
        Transaction trns = null;
        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            dbManager.getDBSession().remove(administrator);
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public boolean collisionExists(String username) {
        boolean collision = false;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            collision = dbManager.getDBSession().createQuery("SELECT a FROM  Administrator a "
                    + "WHERE a.username = :username")
                    .setParameter("username", username)
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
    public Administrator getAdministratorByUsername(String username) {
        Transaction trns = null;
        Administrator result = null;

        try (Session session = dbManager.getDBSession()){
            trns = session.beginTransaction();

            result = dbManager.getDBSession().find(Administrator.class, username);

            trns.commit();
        } catch (RollbackException exc){
            if(trns != null){
                trns.rollback();
            }
        }
        return result;
    }

    @Override
    public Administrator getDefaultAdministrator() {
        return getAdministratorByUsername("admin");
    }
}
