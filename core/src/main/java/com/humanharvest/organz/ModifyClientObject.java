package com.humanharvest.organz;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;

public class ModifyClientObject {

    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private String currentAddress;

    private Region region;
    private Gender gender;
    private BloodType bloodType;
    private Gender genderIdentity;

    private double height;
    private double weight;

    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private Set<String> modifiedFields = new HashSet<>();

    public ModifyClientObject() {}

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        Field[] fields = ModifyClientObject.class.getDeclaredFields();
        List<String> unmodifiedFields = new ArrayList<>();
        for (Field field : fields) {
            if (!modifiedFields.contains(field.getName())) {
                unmodifiedFields.add(field.getName());
            }
        }
        return unmodifiedFields.toArray(new String[0]);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        modifiedFields.add("firstName");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        modifiedFields.add("lastName");
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        modifiedFields.add("middleName");
        this.middleName = middleName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        modifiedFields.add("preferredName");
        this.preferredName = preferredName;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        modifiedFields.add("currentAddress");
        this.currentAddress = currentAddress;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        modifiedFields.add("region");
        this.region = region;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        modifiedFields.add("gender");
        this.gender = gender;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        modifiedFields.add("bloodType");
        this.bloodType = bloodType;
    }

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(Gender genderIdentity) {
        modifiedFields.add("genderIdentity");
        this.genderIdentity = genderIdentity;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        modifiedFields.add("height");
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        modifiedFields.add("weight");
        this.weight = weight;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        modifiedFields.add("dateOfBirth");
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        modifiedFields.add("dateOfDeath");
        this.dateOfDeath = dateOfDeath;
    }
}
