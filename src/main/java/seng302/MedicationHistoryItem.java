package seng302;

import java.time.LocalDateTime;

public class MedicationHistoryItem {
    private String medicationName;
    private LocalDateTime started;
    private LocalDateTime stopped;

    public MedicationHistoryItem(String medicationName, LocalDateTime started, LocalDateTime stopped) {
        this.medicationName = medicationName;
        this.started = started;
        this.stopped = stopped;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public LocalDateTime getStopped() {
        return stopped;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public void setStopped(LocalDateTime stopped) {
        this.stopped = stopped;
    }
}
