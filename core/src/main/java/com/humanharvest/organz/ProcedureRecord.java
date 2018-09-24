package com.humanharvest.organz;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.humanharvest.organz.utilities.enums.Organ;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Table
@Access(AccessType.FIELD)
@DiscriminatorColumn(name = "recordType")
@DiscriminatorValue("base")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransplantRecord.class),
        @JsonSubTypes.Type(value = ProcedureRecord.class)
})
public class ProcedureRecord {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "Client_uid")
    @JsonBackReference
    private Client client;
    @Column(columnDefinition = "text")
    private String summary;
    @Column(columnDefinition = "text")
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

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    /**
     * This method should be called only when this record is added to/removed from a client's collection.
     * Therefore it is package-private so it may only be called from Client.
     * TODO no it's not??
     *
     * @param client The client to set this record as belonging to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String procedureSummary) {
        this.summary = procedureSummary;
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

    /**
     * @return an unmodifiable set of the affected organs
     */
    public Set<Organ> getAffectedOrgans() {
        return Collections.unmodifiableSet(affectedOrgans);
    }

    public void setAffectedOrgans(Set<Organ> affectedOrgans) {
        this.affectedOrgans = affectedOrgans;
    }

    public void addAffectedOrgan(Organ organ) {
        affectedOrgans.add(organ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProcedureRecord)) {
            return false;
        }
        ProcedureRecord record = (ProcedureRecord) o;
        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ProcedureRecord cloneWithoutId() {
        return new ProcedureRecord(getSummary(), getDescription(), getDate());
    }
}
