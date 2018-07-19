package com.humanharvest.organz.resolvers;

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
import com.humanharvest.organz.utilities.enums.Region;

@JsonSerialize(using = ModifyClinicianObjectSerializer.class)
public class ModifyClinicianObject { //TODO discuss how we can create a general serializer to reduce duplicated code.

    private String firstName;
    private String lastName;

    private String middleName;
    private String workAddress;
    private String password;
    private int staffId;
    private Region region;


    @JsonIgnore
    private Set<Field> modifiedFields = new HashSet<>();

    public ModifyClinicianObject() {}

    @JsonIgnore
    public Set<Field> getModifiedFields() {
        return modifiedFields;
    }

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        List<Field> allFields = new ArrayList<>(Arrays.asList(ModifyClinicianObject.class.getDeclaredFields()));
        allFields.removeAll(modifiedFields);
        return allFields.stream().map(Field::getName).toArray(String[]::new);
    }


    @JsonIgnore
    public void registerChange(String fieldName) {
        try {
            modifiedFields.add(ModifyClinicianObject.class.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String toString() {
        String changesText = modifiedFields.stream()
                .map(this::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for clinician.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private String fieldString (Field field) {
        return String.format("Updated %s", field.getName());
    }

}



class ModifyClinicianObjectSerializer extends StdSerializer<ModifyClinicianObject> {

    public ModifyClinicianObjectSerializer() {
        this(null);
    }

    public ModifyClinicianObjectSerializer(Class<ModifyClinicianObject> t) {
        super(t);
    }

    @Override
    public void serialize(ModifyClinicianObject o, JsonGenerator jgen,
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