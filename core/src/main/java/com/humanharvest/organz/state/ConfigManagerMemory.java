package com.humanharvest.organz.state;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.humanharvest.organz.Config;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.utilities.enums.Country;

public class ConfigManagerMemory implements ConfigManager {

    private final Config config;

    public ConfigManagerMemory() {
        config = new Config();
        config.setCountries(EnumSet.of(Country.NZ));
    }

    @Override
    public Set<Country> getAllowedCountries() {
        return Collections.unmodifiableSet(config.getCountries());
    }

    @Override
    public void setAllowedCountries(Set<Country> countries) {
        config.setCountries(EnumSet.copyOf(countries));
    }

    @Override
    public Set<Hospital> getHospitals() {
        return Collections.unmodifiableSet(config.getHospitals());
    }

    @Override
    public void setHospitals(Set<Hospital> hospitals) {
        config.setHospitals(new HashSet<>(hospitals));
    }

    @Override
    public Optional<Hospital> getHospitalById(long id) {
        for (Hospital hospital : getHospitals()) {
            if (hospital.getId().equals(id)) {
                return Optional.of(hospital);
            }
        }
        return Optional.empty();
    }
}
