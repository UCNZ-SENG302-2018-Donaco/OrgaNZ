package com.humanharvest.organz.state;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Country;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ConfigManagerMemory implements ConfigManager {

    private final Config config;

    public ConfigManagerMemory() {
        config = new Config();
        EnumSet<Country> countries = EnumSet.noneOf(Country.class);
        countries.add(Country.NZ);
        config.setCountries(countries);
    }

    @Override
    public Set<Country> getAllowedCountries() {
        return config.getCountries();
    }

    @Override
    public void setAllowedCountries(Set<Country> countries) {
        config.setCountries(countries);
    }

    @Override
    public Set<Hospital> getHospitals() {
        return config.getHospitals();
    }

    @Override
    public void setHospitals(Set<Hospital> hospitals) {
        config.setHospitals(new HashSet<>(hospitals));

    }
}
