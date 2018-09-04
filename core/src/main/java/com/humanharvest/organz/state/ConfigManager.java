package com.humanharvest.organz.state;

import java.util.Optional;
import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;

public interface ConfigManager {

    Set<Country> getAllowedCountries();

    void setAllowedCountries(Set<Country> countries);

    Set<Hospital> getHospitals();

    void setHospitals(Set<Hospital> hospitals);

    Optional<Hospital> getHospitalById(long id);

    void setTransplantProgram(long id, Set<Organ> transplantProgram);

    /**
     * Managers that need to synchronize the config with some external data store can use this method to do so.
     */
    default void applyChangesToConfig() {
        // By default, do nothing.
    }
}
