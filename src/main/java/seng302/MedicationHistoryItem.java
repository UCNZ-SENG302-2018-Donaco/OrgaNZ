package seng302;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicationHistoryItem {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String medicationName;
    private LocalDate started;
    private LocalDate stopped;

    public MedicationHistoryItem(String medicationName, LocalDate started, LocalDate stopped) {
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
}
