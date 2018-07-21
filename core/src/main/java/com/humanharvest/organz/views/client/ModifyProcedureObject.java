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
import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Organ;

@JsonSerialize(using = ModifyProceduresObjectSerializer.class)
public class ModifyProcedureObject {


    private Long id;
    private Client client;
    private String summary;
    private String description;
    private LocalDate date;
    private Organ affectedOrgans;


    @JsonIgnore
    private Set<Field> modifiedFields = new HashSet<>();

    public ModifyProcedureObject() {}

    @JsonIgnore
    public Set<Field> getModifiedFields() {
        return modifiedFields;
    }

    @JsonIgnore
    public String[] getUnmodifiedFields() {
        //Get all fields
        List<Field> allFields = new ArrayList<>(Arrays.asList(ModifyProcedureObject.class.getDeclaredFields()));
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

    public Long getId(Long id) {
        return id;
    }

    public void setId(Long id) {
        registerChange("id");
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        registerChange("client");
        this.client = client;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        registerChange("summary");
        this.summary = summary;
    }

    public String getDescription() { return description;    }

    public void setDescription(String description) {
        registerChange("description");
        this.description = description;
    }

    public LocalDate getDate() { return date;    }

    public void setDate(LocalDate date) {
        registerChange("date");
        this.date = date;
    }

    public Organ getAffectedOrgans() { return affectedOrgans;    }

    public void setAffectedOrgans(Organ affectedOrgans) {
        registerChange("affectedOrgans");
        this.affectedOrgans = affectedOrgans;
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

class ModifyProceduresObjectSerializer extends StdSerializer<ModifyProcedureObject> {

    public ModifyProceduresObjectSerializer() {
        this(null);
    }

    public ModifyProceduresObjectSerializer(Class<ModifyProcedureObject> t) {
        super(t);
    }

    @Override
    public void serialize(ModifyProcedureObject o, JsonGenerator jgen,
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