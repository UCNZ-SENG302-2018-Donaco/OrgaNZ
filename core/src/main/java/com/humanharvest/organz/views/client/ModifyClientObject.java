package com.humanharvest.organz.views.client;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.views.ModifyBaseObject;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyClientObject extends ModifyBaseObject {

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

    private String timeOfDeath;
    private String regionOfDeath;
    private String cityOfDeath;
    private String countryOfDeath;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        registerChange("firstName");
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        registerChange("lastName");
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        registerChange("middleName");
        this.middleName = middleName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        registerChange("preferredName");
        this.preferredName = preferredName;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        registerChange("currentAddress");
        this.currentAddress = currentAddress;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        registerChange("region");
        this.region = region;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        registerChange("gender");
        this.gender = gender;
    }

    public BloodType getBloodType() {
        return bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        registerChange("bloodType");
        this.bloodType = bloodType;
    }

    public Gender getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(Gender genderIdentity) {
        registerChange("genderIdentity");
        this.genderIdentity = genderIdentity;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        registerChange("height");
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        registerChange("weight");
        this.weight = weight;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        registerChange("dateOfBirth");
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        registerChange("dateOfDeath");
        this.dateOfDeath = dateOfDeath;
    }

    public String getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(String timeOfDeath) {
        registerChange("timeOfDeath");
        this.timeOfDeath = timeOfDeath;
    }

    public String getRegionOfDeath() {
        return regionOfDeath;
    }

    public void setRegionOfDeath(String regionOfDeath) {
        registerChange("regionOfDeath");
        this.regionOfDeath = regionOfDeath;
    }

    public String getCityOfDeath() {
        return cityOfDeath;
    }

    public void setCityOfDeath(String cityOfDeath) {
        registerChange("cityOfDeath");
        this.cityOfDeath = cityOfDeath;
    }

    public String getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(String countryOfDeath) {
        registerChange("countryOfDeath");
        this.countryOfDeath = countryOfDeath;
    }

    public String toString() {
        String changesText = getModifiedFields().stream()
                .map(ModifyClientObject::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for client.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private static String fieldString(Member field) {
        return String.format("Updated %s", field.getName());
    }
}
