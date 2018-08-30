package com.humanharvest.organz.utilities.validators.client;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.client.ModifyIllnessObject;

/**
 * Class to ensure that a modified illness is valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class ModifyIllnessValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ModifyIllnessValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check all modified fields and ensure they are valid
     *
     * @param illnessObject the ModifyIllnessObject making changes
     * @return True if all changes are valid
     */
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
            if (illnessObject.getCuredDate() == null) {
                return true;
            }
            if (illnessObject.getDiagnosisDate().isAfter(illnessObject.getCuredDate())) {
                //Diagnosis is after Cured, is invalid
                return false;
            }
        }
        return true;
    }

}
