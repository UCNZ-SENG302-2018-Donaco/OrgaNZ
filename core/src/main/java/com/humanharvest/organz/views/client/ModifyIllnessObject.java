package com.humanharvest.organz.views.client;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ModifyIllnessObject {

  private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
      modifiedFields.add(ModifyIllnessObject.class.getDeclaredField(fieldName));
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
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

class ModifyIllnessObjectSerializer extends StdSerializer<ModifyIllnessObject> {

  public ModifyIllnessObjectSerializer() {
    this(null);
  }

  public ModifyIllnessObjectSerializer(Class<ModifyIllnessObject> t) {
    super(t);
  }

  @Override
  public void serialize(ModifyIllnessObject o, JsonGenerator jgen,
      SerializerProvider serializerProvider) throws IOException {
    jgen.writeStartObject();
    for (Field field : o.getModifiedFields()) {
      try {
        field.setAccessible(true);
        jgen.writeObjectField(field.getName(), field.get(o));
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    jgen.writeEndObject();
  }
}
