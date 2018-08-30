package com.humanharvest.organz.views.client;

import java.time.LocalDate;
import java.util.Set;

import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.utilities.enums.Organ;

public class CreateProcedureView {

    private String summary;
    private String description;
    private LocalDate date;
    private Set<Organ> affectedOrgans;

    public CreateProcedureView(String summary, String description, LocalDate date, Set<Organ> affectedOrgans) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.affectedOrgans = affectedOrgans;
    }

    public CreateProcedureView(ProcedureRecord record) {
        this.summary = record.getSummary();
        this.description = record.getDescription();
        this.date = record.getDate();
        this.affectedOrgans = record.getAffectedOrgans();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<Organ> getAffectedOrgans() {
        return affectedOrgans;
    }

    public void setAffectedOrgans(Set<Organ> affectedOrgans) {
        this.affectedOrgans = affectedOrgans;
    }
}
