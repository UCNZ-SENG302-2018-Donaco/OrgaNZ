package seng302;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an instance of a user taking a medication for a period of time.
 */
public class MedicationRecord {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String medicationName;
    private LocalDate started;
    private LocalDate stopped;

    /**
     * Creates a new MedicationRecord for a given medication name, with the given started date and stopped date.
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

    public String getMedicationName() {
        return medicationName;
    }

    public LocalDate getStarted() {
        return started;
    }

    public LocalDate getStopped() {
        return stopped;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public void setStarted(LocalDate started) {
        this.started = started;
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

    public MedicationRecord copy() {
        return new MedicationRecord(this.medicationName, this.started, this.stopped);
    }
}
