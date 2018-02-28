package seng302;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Donor {

    private final LocalDateTime created_on;
    private LocalDateTime modified_on;
    private String name;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    private String gender;
    private int height;
    private int weight;
    private String bloodType;
    private String currentAddress;
    private String region;

    private int uid;

    public Donor(String name, LocalDate dateOfBirth, int uid) {
        created_on = LocalDateTime.now();
        modified_on = LocalDateTime.now();
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.uid = uid;
    }

    public String getDonorInfoString() {
        return String.format("User: %s. Name: %s, date of birth: %tF, date of death: %tF, gender: %s, height: %scm, weight: %skg, blood type: %s, current address: %s, region: %s, created on: %s, modified on: %s", uid, name, dateOfBirth, dateOfDeath, gender, height, weight, bloodType, currentAddress, region, created_on, modified_on);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getUid() {
        return uid;
    }

    /**
     * Donor objects are identified by their uid
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Donor))
            return false;
        Donor d = (Donor) obj;
        return d.uid == this.uid;
    }
}
