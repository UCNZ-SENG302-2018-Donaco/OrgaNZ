package seng302;

import java.time.LocalDate;

/**
 * Represents a present or past condition of a client
 */
public class Condition {

    private String name;
    private LocalDate diagnosisDate;
    private LocalDate resolutionDate;
    private boolean chronic;

    /**
     * Creates a new Condition for a given condition name and date of diagnosis
     * @param name The name of the condition
     * @param diagnosisDate The date that the client was diagnosed with the condition
     */
    public Condition(String name, LocalDate diagnosisDate) {
        this.name = name;
        this.diagnosisDate = diagnosisDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public boolean isChronic() {
        return chronic;
    }

    public void setChronic(boolean chronic) {
        this.chronic = chronic;
    }
}
