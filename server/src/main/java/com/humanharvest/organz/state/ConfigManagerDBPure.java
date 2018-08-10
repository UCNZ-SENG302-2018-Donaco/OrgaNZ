package com.humanharvest.organz.state;

import java.util.EnumSet;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Country;
import org.hibernate.Transaction;

public class ConfigManagerDBPure implements ConfigManager {

    private final DBManager dbManager;
    private Config configuration;

    public ConfigManagerDBPure() {
        this.dbManager = DBManager.getInstance();
        tryInsertDefault();
    }

    public ConfigManagerDBPure(DBManager dbManager) {
        this.dbManager = dbManager;
        tryInsertDefault();
    }

    private void tryInsertDefault() {
        Config config = new Config();
        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        countries.add(Country.NZ);
        config.setCountries(countries);
        try {
            dbManager.saveEntity(config);
            configuration = config;
        } catch (PersistenceException ignored) {
            getConfig();
        }
    }

    private Config getConfig() {
        Transaction trns = null;
        Config config = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            config = dbManager.getDBSession()
                    .createQuery("FROM Config", Config.class)
                    .getSingleResult();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        configuration = config;
        return config;
    }

    @Override
    public EnumSet<Country> getAllowedCountries() {
        Transaction trns = null;
        Config config = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();
            config = dbManager.getDBSession()
                    .createQuery("FROM Config", Config.class)
                    .getSingleResult();
            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }

        configuration = config;

        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        if (config != null) {
            countries.addAll(config.getCountries());
        }
        return countries;
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> countries) {

        configuration.setCountries(countries);

        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().update(configuration);

            trns.commit();
        } catch (RollbackException exc) {
            if (trns != null) {
                trns.rollback();
            }
        }
    }
}
