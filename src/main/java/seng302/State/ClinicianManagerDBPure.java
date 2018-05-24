package seng302.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.RollbackException;

import seng302.Clinician;
import seng302.Database.DBManager;
import seng302.Utilities.Enums.Region;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClinicianManagerDBPure implements ClinicianManager {

    private final DBManager dbManager;
    private Clinician defaultClinician = new Clinician("admin", null, "admin", "admin", Region.UNSPECIFIED,
            0, "admin");


    public ClinicianManagerDBPure(){
        this.dbManager = DBManager.getInstance();
        dbManager.saveEntity(defaultClinician);
    }

    public ClinicianManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
        dbManager.saveEntity(defaultClinician);
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
    public void applyChangesTo(Clinician clinician) {
        Transaction trns = null;

        try (Session session = dbManager.getDBSession()) {
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
        Transaction trns = null;
        Clinician result = null;

        try (Session session = dbManager.getDBSession()){
            trns = session.beginTransaction();

            result = dbManager.getDBSession().find(Clinician.class, id);

            trns.commit();
        } catch (RollbackException exc){
            if(trns != null){
                trns.rollback();
            }
        }
        return result;
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
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(0);
    }
}
