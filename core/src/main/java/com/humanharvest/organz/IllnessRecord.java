package com.humanharvest.organz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents an instance of a user having an illness for a period of time.
 */
@Entity
@Table
@Access(AccessType.FIELD)
public class IllnessRecord {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_uid")
    @JsonBackReference
    private Client client;
    private String illnessName;
    private LocalDate diagnosisDate;
    private LocalDate curedDate;
    private boolean isChronic;

    protected IllnessRecord() {
    }

    /**
     * Creates a new IllnessRecord for a given illness.
     * @param illnessName The name of the illness.
     * @param diagnosisDate The date the illness was diagnosed for the client.
     * @param curedDate The date the illness was cured.
     * @param isChronic Whether the illness is chronic or not.
     */
    public IllnessRecord(String illnessName, LocalDate diagnosisDate, LocalDate curedDate, boolean isChronic) {
        this.illnessName = illnessName;
        this.diagnosisDate = diagnosisDate;
        this.curedDate = curedDate;
        this.isChronic = isChronic;
    }

    /**
     * Creates a new IllnessRecord for a given illness.
     * @param illnessName The name of the illness.
     * @param diagnosisDate The date the illness was diagnosed for the client.
     * @param isChronic Whether the illness is chronic or not.
     */
    public IllnessRecord(String illnessName, LocalDate diagnosisDate, boolean isChronic) {
        this.illnessName = illnessName;
        this.diagnosisDate = diagnosisDate;
        this.curedDate = null;
        this.isChronic = isChronic;
    }

    public Client getClient() {
        return client;
    }

    public String getIllnessName() {
        return illnessName;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public LocalDate getCuredDate() {
        return curedDate;
    }

    public boolean isChronic() {
        return isChronic;
    }

    /**
     * This method should be called only when this record is added to/removed from a client's collection.
     * Therefore it is package-private so it may only be called from Client.
     * @param client The client to set this record as belonging to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public void setCuredDate(LocalDate curedDate) {
        this.curedDate = curedDate;
    }

    public void setChronic(boolean chronic) {
        isChronic = chronic;
    }

    public String toString() {
        if (curedDate == null && !isChronic) {
            return String.format("%s Diagnosed on: %s", illnessName, diagnosisDate.format(dateFormat));
        }
        if (isChronic) {
            return String.format("%s (Chronic Disease) Diagnosed on: %s", illnessName,
                    diagnosisDate.format(dateFormat));
        } else {
            return String.format("%s Diagnosed on: %s, Cured on: %s)", illnessName,
                    diagnosisDate.format(dateFormat), curedDate.format(dateFormat));
        }
    }
}
