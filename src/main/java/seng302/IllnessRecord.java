package seng302;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents an instance of a user having an illness for a period of time.
 */
public class IllnessRecord {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String illnessName;
    private LocalDate diagnosisDate;
    private LocalDate curedDate;
    private boolean isChronic;

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
