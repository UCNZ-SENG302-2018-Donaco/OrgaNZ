package com.humanharvest.organz;

import java.util.EnumSet;

import com.humanharvest.organz.utilities.enums.Country;

public class Config {

    EnumSet<Country> countries;

    public Config(EnumSet<Country> countries) {
        this.countries = countries;
    }

    public EnumSet<Country> getCountries() {
        return countries;
    }

    public void setCountries(EnumSet<Country> countries) {
        this.countries = countries;
    }
}
