package com.humanharvest.organz.views.administrator;

import java.io.IOException;
import java.lang.reflect.Field;
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

@JsonSerialize(using = ModifyAdministratorObjectSerializer.class)
public class ModifyAdministratorObject {

    private String username;
    private String password;

    @JsonIgnore
    private Set<Field> modifiedFields = new HashSet<>();

    public ModifyAdministratorObject() {}

    @JsonIgnore
    public Set<Field> getModifiedFields() {
        return modifiedFields;
    }

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        //Get all fields
        List<Field> allFields = new ArrayList<>(Arrays.asList(ModifyAdministratorObject.class.getDeclaredFields()));
        //Remove the ones that have been modified
        allFields.removeAll(modifiedFields);
        //Convert to a list of strings
        return allFields.stream().map(Field::getName).toArray(String[]::new);
    }

    @JsonIgnore
    public void registerChange(String fieldName) {
        try {
            modifiedFields.add(ModifyAdministratorObject.class.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        String changesText = modifiedFields.stream()
                .map(this::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for administrator.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private String fieldString (Field field) {
        return String.format("Updated %s", field.getName());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        registerChange("username");
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        registerChange("password");
        this.password = password;
    }
}

class ModifyAdministratorObjectSerializer extends StdSerializer<ModifyAdministratorObject> {

    public ModifyAdministratorObjectSerializer() {
        this(null);
    }

    public ModifyAdministratorObjectSerializer(Class<ModifyAdministratorObject> t) {
        super(t);
    }

    @Override
    public void serialize(ModifyAdministratorObject o, JsonGenerator jgen,
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