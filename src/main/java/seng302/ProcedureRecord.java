package seng302;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import seng302.Utilities.Enums.Organ;

public class ProcedureRecord {

    private String summary;
    private String description;
    private LocalDate date;
    private Set<Organ> affectedOrgans = EnumSet.noneOf(Organ.class);

    public ProcedureRecord(String summary, String description, LocalDate date) {
        this.summary = summary;
        this.description = description;
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Set<Organ> getAffectedOrgans() {
        return affectedOrgans;
    }

    public void setSummary(String procedureSummary) {
        this.summary = procedureSummary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setAffectedOrgans(Set<Organ> affectedOrgans) {
        this.affectedOrgans = affectedOrgans;
    }
}
