package com.humanharvest.organz.views.client;

import com.humanharvest.organz.Client;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CreateIllnessView {

  private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private Long id;
  private String illnessName;
  private LocalDate diagnosisDate;
  private LocalDate curedDate;
  private boolean isChronic;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public LocalDate getCuredDate() {
    return curedDate;
  }

  public void setCuredDate(LocalDate curedDate) {
    this.curedDate = curedDate;
  }

  public boolean isChronic() {
    return isChronic;
  }

  public void setChronic(boolean chronic) {
    isChronic = chronic;
  }
}
