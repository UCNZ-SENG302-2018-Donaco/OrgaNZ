package com.humanharvest.organz.state;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.RollbackException;

import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Country;
import org.hibernate.Transaction;

public class ConfigManagerDBPure implements ConfigManager {

    private final DBManager dbManager;

    public ConfigManagerDBPure() {
        this.dbManager = DBManager.getInstance();
    }

    @Override
    public EnumSet<Country> getAllowedCountries() {
        Transaction trns = null;
        List<Country> countries = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            countries = dbManager.getDBSession()
                    .createQuery("FROM Country", Country.class)
                    .getResultList();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        EnumSet<Country> countrySet = EnumSet.noneOf(Country.class);
        if (countries != null) {
            countrySet.addAll(countries);
        }

        return countrySet;
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> countries) {
        Transaction trns = null;
        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            session.createQuery("DELETE FROM Country").executeUpdate();

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
