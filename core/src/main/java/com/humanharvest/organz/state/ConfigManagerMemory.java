package com.humanharvest.organz.state;

import java.util.EnumSet;

import com.humanharvest.organz.utilities.enums.Country;

public class ConfigManagerMemory implements ConfigManager {

    private EnumSet<Country> allowedCountries;

    public ConfigManagerMemory() {
        allowedCountries = EnumSet.noneOf(Country.class);
        allowedCountries.add(Country.NZ);
        allowedCountries.add(Country.ZA);
    }

    @Override
    public EnumSet<Country> getAllowedCountries() {
        return allowedCountries;
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> allowedCountries) {
        this.allowedCountries = allowedCountries;
    }
}
