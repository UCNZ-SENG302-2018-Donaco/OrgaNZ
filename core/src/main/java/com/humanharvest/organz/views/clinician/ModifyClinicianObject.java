package com.humanharvest.organz.views.clinician;

import java.lang.reflect.Member;
import java.util.stream.Collectors;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.views.ModifyBaseObject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyClinicianObject extends ModifyBaseObject {

    private String firstName;
    private String lastName;

    private String middleName;
    private String workAddress;
    private String password;
    private String region;
    private Country country;

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

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        registerChange("workAddress");
        this.workAddress = workAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        registerChange("password");
        this.password = password;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        registerChange("region");
        this.region = region;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        registerChange("country");
        this.country = country;
    }

    public String toString() {
        String changesText = getModifiedFields().stream()
                .map(ModifyClinicianObject::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for clinician.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private static String fieldString(Member field) {
        return String.format("Updated %s", field.getName());
    }
}