package com.humanharvest.organz.state;

import java.util.EnumSet;
import java.util.Set;

import com.humanharvest.organz.utilities.enums.Country;

public interface ConfigManager {

    Set<Country> getAllowedCountries();

    void setAllowedCountries(EnumSet<Country> countries);

}
