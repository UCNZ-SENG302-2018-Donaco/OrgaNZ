package com.humanharvest.organz.state;

import java.util.EnumSet;
import java.util.Set;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.utilities.enums.Country;

public class ConfigManagerMemory implements ConfigManager {

    private Config config;

    public ConfigManagerMemory() {
        config = new Config();
    }

    @Override
    public Set<Country> getAllowedCountries() {
        return config.getCountries();
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> allowedCountries) {
        config.setCountries(allowedCountries);
    }
}
