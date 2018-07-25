package com.humanharvest.organz.state;

import java.util.EnumSet;
import java.util.List;

import com.humanharvest.organz.utilities.enums.Country;

public interface ConfigManager {

    EnumSet<Country> getAllowedCountries();

    void setAllowedCountries(EnumSet<Country> countries);

}
