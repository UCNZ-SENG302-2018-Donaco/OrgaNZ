package com.humanharvest.organz.views.client;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.ModifyBaseObject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyProcedureObject extends ModifyBaseObject {

    private Long id;
    private Client client;
    private String summary;
    private String description;
    private LocalDate date;
    private Set<Organ> affectedOrgans;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        registerChange("description");
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        registerChange("date");
        this.date = date;
    }

    public Set<Organ> getAffectedOrgans() {
        return affectedOrgans;
    }

    public void setAffectedOrgans(Set<Organ> affectedOrgans) {
        registerChange("affectedOrgans");
        this.affectedOrgans = affectedOrgans;
    }

    public String toString() {
        String changesText = modifiedFields.stream()
                .map(ModifyProcedureObject::fieldString)
                .collect(Collectors.joining("\n"));

        return String.format("Updated details for client.\n"
                        + "These changes were made: \n\n%s",
                changesText);
    }

    private static String fieldString(Field field) {
        return String.format("Updated %s", field.getName());
    }
}