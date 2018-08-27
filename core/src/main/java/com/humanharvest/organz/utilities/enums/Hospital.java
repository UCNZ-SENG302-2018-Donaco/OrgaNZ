package com.humanharvest.organz.utilities.enums;

import java.util.Collection;
import java.util.HashSet;

public class Hospital {

    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private Collection<Organ> organs;

    Hospital(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.organs = new HashSet<>();
    }

    Hospital(String name, double latitude, double longitude, String address, Collection<Organ> organs) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.organs = organs;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public Collection<Organ> getOrgans() {
        return organs;
    }

    public void setOrgans(Collection<Organ> organs) {
        this.organs = new HashSet<>(organs);
    }

    public boolean addOrgan(Organ organ) {
        return organs.add(organ);
    }

    public boolean removeOrgan(Organ organ) {
        return organs.remove(organ);
    }
}
