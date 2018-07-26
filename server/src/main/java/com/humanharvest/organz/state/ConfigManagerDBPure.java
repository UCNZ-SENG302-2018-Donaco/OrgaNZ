package com.humanharvest.organz.state;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.RollbackException;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Country;
import org.hibernate.Transaction;

public class ConfigManagerDBPure implements ConfigManager {

    private final DBManager dbManager;

    public ConfigManagerDBPure() {
        this.dbManager = DBManager.getInstance();
//
//        Transaction trns = null;
//
//        try (org.hibernate.Session session = dbManager.getDBSession()) {
//            trns = session.beginTransaction();
//
//            dbManager.getDBSession().update(State.getConfig());
//
//            trns.commit();
//        } catch (RollbackException exc) {
//            if (trns != null) {
//                trns.rollback();
//            }
//        }
    }

    @Override
    public EnumSet<Country> getAllowedCountries() {
        Transaction trns = null;
        List<Config> countries = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            countries = dbManager.getDBSession()
                    .createQuery("FROM Config", Config.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        EnumSet<Country> countrySet = EnumSet.noneOf(Country.class);
        if (countries != null) {
            countrySet.addAll(countries.get(0).getCountries());
        }

        return countrySet;
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> countries) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            session.createQuery("DELETE FROM Config").executeUpdate();

            for (Country country : countries) {
                session.save(country);
            }

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }
}
