package com.humanharvest.organz.views.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;

@JsonSerialize(using = ModifyClientObjectSerializer.class)
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

    @JsonIgnore
    private Set<Field> modifiedFields = new HashSet<>();

    public ModifyClientObject() {}

    @JsonIgnore
    public Set<Field> getModifiedFields() {
        return modifiedFields;
    }

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        //Get all fields
        List<Field> allFields = new ArrayList<>(Arrays.asList(ModifyClientObject.class.getDeclaredFields()));
        //Remove the ones that have been modified
        allFields.removeAll(modifiedFields);
        //Convert to a list of strings
        return allFields.stream().map(Field::getName).toArray(String[]::new);
    }

    @JsonIgnore
    public void registerChange(String fieldName) {
        try {
            modifiedFields.add(ModifyClientObject.class.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
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

    public String toString() {
        String changesText = modifiedFields.stream()
                .map(this::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for client.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private String fieldString (Field field) {
        return String.format("Updated %s", field.getName());
    }
}

class ModifyClientObjectSerializer extends StdSerializer<ModifyClientObject> {

    public ModifyClientObjectSerializer() {
        this(null);
    }

    public ModifyClientObjectSerializer(Class<ModifyClientObject> t) {
        super(t);
    }

    @Override
    public void serialize(ModifyClientObject o, JsonGenerator jgen,
            SerializerProvider serializerProvider) throws IOException {
        jgen.writeStartObject();
        for (Field field : o.getModifiedFields()) {
            try {
                field.setAccessible(true);
                jgen.writeObjectField(field.getName(), field.get(o));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        jgen.writeEndObject();
    }
}