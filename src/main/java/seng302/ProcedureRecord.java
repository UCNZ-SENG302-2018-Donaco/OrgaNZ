package seng302;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import seng302.Utilities.Enums.Organ;

@Entity
@Table
public class ProcedureRecord {

    @Id
    @GeneratedValue
    private long id;
    private String summary;
    private String description;
    private LocalDate date;
    @ElementCollection(targetClass = Organ.class)
    @Enumerated(EnumType.STRING)
    private Set<Organ> affectedOrgans = EnumSet.noneOf(Organ.class);

    protected ProcedureRecord() {
    }

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
