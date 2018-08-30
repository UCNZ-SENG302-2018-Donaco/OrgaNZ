package com.humanharvest.organz.views.client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.humanharvest.organz.views.ModifyBaseObject;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = ModifyBaseObject.Serialiser.class)
public class ModifyIllnessObject extends ModifyBaseObject {

    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String illnessName;
    private LocalDate diagnosisDate;
    private LocalDate curedDate;
    private boolean isChronic;

    public String getIllnessName() {
        return illnessName;
    }

    public void setIllnessName(String illnessName) {
        registerChange("illnessName");
        this.illnessName = illnessName;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        registerChange("diagnosisDate");
        this.diagnosisDate = diagnosisDate;
    }

    public LocalDate getCuredDate() {
        return curedDate;
    }

    public void setCuredDate(LocalDate curedDate) {
        registerChange("curedDate");
        this.curedDate = curedDate;
    }

    public boolean isChronic() {
        return isChronic;
    }

    public void setChronic(boolean chronic) {
        registerChange("isChronic");
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
