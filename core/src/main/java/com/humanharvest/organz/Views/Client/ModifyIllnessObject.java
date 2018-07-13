package com.humanharvest.organz.Views.Client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanharvest.organz.Client;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifyIllnessObject {

  private Long id;
  private Client client;
  private String illnessName;
  private LocalDate diagnosisDate;
  private LocalDate curedDate;
  private boolean isChronic;

  @JsonIgnore
  private Set<Field> modifiedFields = new HashSet<>();

  public ModifyIllnessObject() {}

  @JsonIgnore
  public Set<Field> getModifiedFields() {return modifiedFields;}

  @JsonIgnore
  public String[] getUnmodifiedFields() {
    //Get all fields
    List<Field> allFields = new ArrayList<>(Arrays.asList(ModifyIllnessObject.class.getDeclaredFields()));
    //Remove the ones that have been modified
    allFields.removeAll(modifiedFields);
    //Convert to a list of strings
    return allFields.stream().map(Field::getName).toArray(String[]::new);
  }


  @JsonIgnore
  public void registerChange(String fieldName) {
    try {
      modifiedFields.add(ModifyClientObject.class.getDeclaredField(fieldName));
      System.out.println(modifiedFields.toString());
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  public Long getId() {

    return id;
  }

  public void setId(Long id) {
    registerChange("id");
    this.id = id;
  }

  public Client getClient() {
    return client;
  }

  public void setClient(Client client) {
    registerChange("client");
    this.client = client;
  }

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
    registerChange("chronic");
    isChronic = chronic;
  }
}
