package com.humanharvest.organz.state;

import com.humanharvest.organz.utilities.enums.Country;
import java.util.Set;

public interface ConfigManager {

    Set<Country> getAllowedCountries();

    void setAllowedCountries(Set<Country> countries);
}
