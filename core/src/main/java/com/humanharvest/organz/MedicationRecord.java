package com.humanharvest.organz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents an instance of a user taking a medication for a period of time.
 */
@Entity
@Table
@Access(AccessType.FIELD)
public class MedicationRecord implements Comparable<MedicationRecord> {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "Client_uid")
    @JsonBackReference
    private Client client;
    private String medicationName;
    private LocalDate started;
    private LocalDate stopped;

    protected MedicationRecord() {
    }

    /**
     * Creates a new MedicationRecord for a given medication name, with the given started date and stopped date.
     *
     * @param medicationName The name of the medication to create a record for.
     * @param started The date on which the user started taking this medication.
     * @param stopped The date on which the user stopped taking this medication. If null, means the user is still
     * taking this medication.
     */
    public MedicationRecord(String medicationName, LocalDate started, LocalDate stopped) {
        this.medicationName = medicationName;
        this.started = started;
        this.stopped = stopped;
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
     *
     * @param client The client to set this record as belonging to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public LocalDate getStarted() {
        return started;
    }

    public void setStarted(LocalDate started) {
        this.started = started;
    }

    public LocalDate getStopped() {
        return stopped;
    }

    public void setStopped(LocalDate stopped) {
        this.stopped = stopped;
    }

    public String toString() {
        if (stopped == null) {
            return String.format("%s (started using: %s)", medicationName, started.format(dateFormat));
        } else {
            return String.format("%s (started using: %s, stopped using: %s)", medicationName,
                    started.format(dateFormat), stopped.format(dateFormat));
        }
    }

    /**
     * Medication Records are first ordered by their name. If the names are the same, they will be ordered by their ID
     *
     * @param other The other MedicationRecord to compare
     * @return Which record is higher in the sort
     */
    @Override
    public int compareTo(MedicationRecord other) {
        int comparison = this.medicationName.compareTo(other.medicationName);
        if (comparison == 0) {
            return this.id.compareTo(other.id);
        } else {
            return comparison;
        }
    }

    /**
     * Medication records are considered equal if their name and ID are the same
     *
     * @param o The other object to compare to
     * @return If the other object is a match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MedicationRecord)) {
            return false;
        }
        MedicationRecord other = (MedicationRecord) o;
        return this.medicationName.equals(other.medicationName) && id.equals(other.id);
    }

    /**
     * Implement the hashCode function on name and ID to reflect equals
     *
     * @return A hash value generated
     */
    @Override
    public int hashCode() {
        return Objects.hash(medicationName, id);
    }
}
