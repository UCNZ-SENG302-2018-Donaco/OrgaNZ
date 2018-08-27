package com.humanharvest.organz;

import com.humanharvest.organz.utilities.enums.Country;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Config {

    @Id
    private int id = 0;

    @ElementCollection(targetClass = Country.class)
    @Enumerated(EnumType.STRING)
    Set<Country> countries;

    @OneToMany(
            mappedBy = "config",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<Hospital> hospitals;

    public Config() {
        this.countries = EnumSet.noneOf(Country.class);
        this.hospitals = new HashSet<>();
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    public Set<Hospital> getHospitals() {
        return hospitals;
    }

    public void setHospitals(Set<Hospital> hospitals) {
        this.hospitals = hospitals;
    }
}
