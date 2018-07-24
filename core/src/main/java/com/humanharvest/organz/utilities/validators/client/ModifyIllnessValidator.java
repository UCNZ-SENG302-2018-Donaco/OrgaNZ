package com.humanharvest.organz.utilities.validators.client;

import com.humanharvest.organz.views.client.ModifyIllnessObject;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ModifyIllnessValidator {

  public static boolean isValid(ModifyIllnessObject illnessObject) {
    //Get a list of unmodified fields so we don't check fields that haven't changed
    List<String> unmodifiedFields = Arrays.asList(illnessObject.getUnmodifiedFields());

    //Check that the illnessName is not being set to null or empty strings
    if (!unmodifiedFields.contains("illnessName") &&
        NotEmptyStringValidator.isInvalidString(illnessObject.getIllnessName())) {
      return false;
    }
    //Check that the dates are not in the future
    if (!unmodifiedFields.contains("diagnosisDate") &&
        illnessObject.getDiagnosisDate().isAfter(LocalDate.now())) {
      return false;
    }
    //If both dates have been modified, check they are not inconsistent. If only one or the other then it will
    // need to be checked against the client object later
    if (!unmodifiedFields.contains("diagnosisDate") && !unmodifiedFields.contains("curedDate")) {
      if (illnessObject.getDiagnosisDate().isAfter(illnessObject.getCuredDate())) {
        //Diagnosis is after Cured, is invalid
        return false;
      }
    }
    return true;
  }

}
