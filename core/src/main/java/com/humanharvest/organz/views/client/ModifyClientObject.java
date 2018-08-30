package com.humanharvest.organz.views.client;

import static com.humanharvest.organz.utilities.type_converters.StringFormatter.unCamelCase;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;

import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.views.ModifyBaseObject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyClientObject extends ModifyBaseObject {

    private String firstName;
    private String lastName;
    private String middleName;
    private String preferredName;
    private String currentAddress;
    private Country country;

    private String region;
    private Gender gender;
    private BloodType bloodType;
    private Gender genderIdentity;

    private double height;
    private double weight;

    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;

    private LocalTime timeOfDeath;
    private String regionOfDeath;
    private String cityOfDeath;
    private Country countryOfDeath;

    private static String fieldString(Member field) {
        return String.format("Updated %s", unCamelCase(field.getName()));
    }

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
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

    public LocalTime getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(LocalTime timeOfDeath) {
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

    public Country getCountryOfDeath() {
        return countryOfDeath;
    }

    public void setCountryOfDeath(Country countryOfDeath) {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        registerChange("country");
        this.country = country;
    }
}
