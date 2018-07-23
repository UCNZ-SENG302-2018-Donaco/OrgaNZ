package com.humanharvest.organz.views.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateIllnessView {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String illnessName;
    private LocalDate diagnosisDate;
    private boolean isChronic;

    public CreateIllnessView() {
    }

    public CreateIllnessView(String illnessName, LocalDate diagnosisDate, boolean isChronic) {
        this.illnessName = illnessName;
        this.diagnosisDate = diagnosisDate;
        this.isChronic = isChronic;
    }

    public String getIllnessName() {
        return illnessName;
    }

    public void setIllnessName(String illnessName) {
        this.illnessName = illnessName;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public boolean isChronic() {
        return isChronic;
    }

    public void setChronic(boolean chronic) {
        isChronic = chronic;
    }
}
