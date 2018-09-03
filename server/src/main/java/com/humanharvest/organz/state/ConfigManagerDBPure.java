package com.humanharvest.organz.state;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.database.DBManager;
import com.humanharvest.organz.utilities.enums.Country;

import org.hibernate.Transaction;

public class ConfigManagerDBPure implements ConfigManager {

    private static final Logger LOGGER = Logger.getLogger(ConfigManagerDBPure.class.getName());

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

        // Set default countries
        Set<Country> countries = EnumSet.noneOf(Country.class);
        countries.add(Country.NZ);
        config.setCountries(countries);

        // Set default hospitals
        config.setHospitals(Hospital.getDefaultHospitals());

        // Try and save the config, otherwise just use what's already there
        try {
            dbManager.saveEntity(config);
            configuration = config;
        } catch (PersistenceException exc) {
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
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }

        configuration = config;
        return config;
    }

    @Override
    public void applyChangesToConfig() {
        Transaction trns = null;

        try (org.hibernate.Session session = dbManager.getDBSession()) {
            trns = session.beginTransaction();

            dbManager.getDBSession().update(configuration);

            trns.commit();
        } catch (RollbackException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            if (trns != null) {
                trns.rollback();
            }
        }
    }

    @Override
    public Set<Country> getAllowedCountries() {
        Config config = getConfig();
        configuration = config;

        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        if (config != null) {
            countries.addAll(config.getCountries());
        }
        return countries;
    }

    @Override
    public void setAllowedCountries(Set<Country> countries) {
        configuration.setCountries(countries);
        applyChangesToConfig();
    }

    @Override
    public Set<Hospital> getHospitals() {
        Config config = getConfig();
        configuration = config;
        return Collections.unmodifiableSet(config.getHospitals());
    }

    @Override
    public void setHospitals(Set<Hospital> hospitals) {
        configuration.setHospitals(hospitals);
        applyChangesToConfig();
    }

    @Override
    public Optional<Hospital> getHospitalById(long id) {
        for (Hospital hospital : getHospitals()) {
            if (hospital.getId().equals(id)) {
                return Optional.of(hospital);
            }
        }
        return Optional.empty();
    }
}
