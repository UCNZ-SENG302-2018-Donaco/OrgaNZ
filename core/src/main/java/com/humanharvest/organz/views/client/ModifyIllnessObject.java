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
    private Boolean isChronic;

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

    public Boolean getIsChronic() {
        return isChronic;
    }

    public void setIsChronic(boolean chronic) {
        registerChange("isChronic");
        isChronic = chronic;
    }
}
