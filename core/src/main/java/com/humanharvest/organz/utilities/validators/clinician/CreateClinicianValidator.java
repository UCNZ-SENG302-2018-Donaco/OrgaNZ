package com.humanharvest.organz.utilities.validators.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;

/**
 * Class to ensure that a clinician is valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class CreateClinicianValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private CreateClinicianValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks a Clinician for validity on the following:
     * First and Last names are not empty
     * Password is not empty
     *
     * @param clinician The Clinician to check
     * @return True if all validity checks pass
     */
    public static boolean isValid(Clinician clinician) {
        if (NotEmptyStringValidator.isInvalidString(clinician.getFirstName())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(clinician.getLastName())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(clinician.getPassword())) {
            return false;
        }

        return true;

    }
}
