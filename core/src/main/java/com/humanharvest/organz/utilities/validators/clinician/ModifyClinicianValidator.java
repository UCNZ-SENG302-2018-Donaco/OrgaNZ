package com.humanharvest.organz.utilities.validators.clinician;

import java.util.Arrays;
import java.util.List;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;

/**
 * Class to ensure that a modified clinician is valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class ModifyClinicianValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ModifyClinicianValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks a ModifyClinicianObject and ensures that all required strings are valid
     *
     * @param clinician The ModifyClinicianObject to check
     * @return True if all validity checks pass
     */
    public static boolean isValid(ModifyClinicianObject clinician) {

        List<String> unmodifiedFields = Arrays.asList(clinician.getUnmodifiedFields());

        if (!unmodifiedFields.contains("firstName") &&
                NotEmptyStringValidator.isInvalidString(clinician.getFirstName())) {
            return false;
        }
        if (!unmodifiedFields.contains("lastName") &&
                NotEmptyStringValidator.isInvalidString(clinician.getLastName())) {
            return false;
        }

        if (!unmodifiedFields.contains("workAddress") &&
                NotEmptyStringValidator.isInvalidString(clinician.getWorkAddress())) {
            return false;
        }

        if (!unmodifiedFields.contains("password") &&
                NotEmptyStringValidator.isInvalidString(clinician.getPassword())) {
            return false;
        }

        return true;
    }
}
