package com.humanharvest.organz;

import com.humanharvest.organz.utilities.enums.Country;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table
public class Config {

    @Id
    private int id = 0;

    @ElementCollection(targetClass = Country.class)
    @Enumerated(EnumType.STRING)
    Set<Country> countries;

    public Config() {
        this.countries = EnumSet.noneOf(Country.class);
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }
}
