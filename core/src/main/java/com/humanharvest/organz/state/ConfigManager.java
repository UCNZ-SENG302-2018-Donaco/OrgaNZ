package com.humanharvest.organz.state;

import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Country;

public interface ConfigManager {

    Set<Country> getAllowedCountries();

    void setAllowedCountries(Set<Country> countries);

    Set<Hospital> getHospitals();

    void setHospitals(Set<Hospital> hospitals);
}
