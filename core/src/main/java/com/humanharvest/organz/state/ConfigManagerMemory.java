package com.humanharvest.organz.state;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.utilities.enums.Country;

import java.util.EnumSet;
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
}
