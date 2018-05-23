package seng302.State;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.RollbackException;

import seng302.Client;
import seng302.Clinician;
import seng302.Database.DBManager;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClinicianManagerDBPure implements ClinicianManager{

    private final DBManager dbManager;

    public ClinicianManagerDBPure(){this.dbManager = DBManager.getInstance();}

    public ClinicianManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    @Override
    public List<Clinician> getClinicians() {
        List<Clinician> clinicians = null;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
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
        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

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
    public void addClinician(Clinician clinician) {
        dbManager.saveEntity(clinician);
    }

    @Override
    public void removeClinician(Clinician clinician) {
        Transaction trns = null;
        try (Session session = dbManager.getDBSession()) {
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
    public Clinician getClinicianByStaffId(int id) {
        return dbManager.getDBSession().find(Clinician.class, id);
    }

    @Override
    public boolean collisionExists(int id) {
        boolean collision = false;
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            collision = dbManager.getDBSession().createQuery("SELECT c from  Clinician c Where c.id = :id")
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
    public Clinician getDefaultClinician(){
        return dbManager.getDBSession().find(Clinician.class,0);
    }

}
