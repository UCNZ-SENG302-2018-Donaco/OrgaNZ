package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Region;
import org.hibernate.Transaction;

public class ClinicianManagerDBPure implements ClinicianManager {

    private final DBManager dbManager;
    private Clinician defaultClinician = new Clinician(
            "Default",
            null,
            "Clinician",
            "Unspecified",
            Region.UNSPECIFIED.name(),
            null,
            0,
            "clinician");

    public ClinicianManagerDBPure(){
        this.dbManager = DBManager.getInstance();
        tryInsertDefault();
    }

    public ClinicianManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
        tryInsertDefault();
    }

    private void tryInsertDefault() {
        try {
            dbManager.saveEntity(defaultClinician);
        } catch (PersistenceException ignored) {
        }
    }

    @Override
    public List<Clinician> getClinicians() {
        List<Clinician> clinicians = null;
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            clinicians = dbManager.getDBSession()
                    .createQuery("FROM Clinician ", Clinician.class)
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
    public void setClinicians(Collection<Clinician> clinicians) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            session.createQuery("DELETE FROM Clinician").executeUpdate();

            for (Clinician clinician : clinicians) {
                session.save(clinician);
            }

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public void applyChangesTo(Clinician clinician) {
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().update(clinician);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public void addClinician(Clinician clinician) {
        dbManager.saveEntity(clinician);
    }

    @Override
    public void removeClinician(Clinician clinician) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            dbManager.getDBSession().remove(clinician);
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public Optional<Clinician> getClinicianByStaffId(int id) {
        Transaction trns = null;
        Clinician result = null;

        try (org.hibernate.Session session = dbManager.getDBSession()){
            trns = session.beginTransaction();

            result = dbManager.getDBSession().find(Clinician.class, id);

            trns.commit();
        } catch (RollbackException exc){
            if(trns != null){
                trns.rollback();
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public boolean doesStaffIdExist(int id) {
        boolean collision = false;
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            collision = dbManager.getDBSession().createQuery("SELECT c from Clinician c Where c.id = :id")
                    .setParameter("id", id)
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
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(0).orElseThrow(IllegalStateException::new);
    }
}
