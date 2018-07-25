package com.humanharvest.organz.state;

import java.util.EnumSet;

import com.humanharvest.organz.utilities.enums.Country;

public class ConfigManagerDBPure implements ConfigManager {

    @Override
    public EnumSet<Country> getAllowedCountries() {
        return null;
    }

    @Override
    public void setAllowedCountries(EnumSet<Country> countries) {

    }
}
