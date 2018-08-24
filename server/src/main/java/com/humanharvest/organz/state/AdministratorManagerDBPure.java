package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.database.DBManager;
import org.hibernate.Transaction;

import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

        try (org.hibernate.Session session = dbManager.getDBSession()) {
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
    public Iterable<Administrator> getAdministratorsFiltered(String nameQuery, Integer offset, Integer count) {
        // TODO: Implement this so it's optimised
        return AdministratorManager.super.getAdministratorsFiltered(nameQuery, offset, count);
    }

    @Override
    public void removeAdministrator(Administrator administrator) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
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
    public boolean doesUsernameExist(String username) {
        boolean collision = false;
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            collision = !dbManager.getDBSession().createQuery("SELECT a FROM Administrator a "
                    + "WHERE a.username = :username")
                    .setParameter("username", username)
                    .getResultList().isEmpty();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        return collision;
    }

    @Override
    public Optional<Administrator> getAdministratorByUsername(String username) {
        Transaction trns = null;
        Administrator result = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            result = dbManager.getDBSession().find(Administrator.class, username);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        return Optional.ofNullable(result);
    }

    @Override
    public Administrator getDefaultAdministrator() {
        return getAdministratorByUsername("admin").orElseThrow(RuntimeException::new);
    }

    @Override
    public void applyChangesTo(Administrator administrator) {
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().update(administrator);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }
}
