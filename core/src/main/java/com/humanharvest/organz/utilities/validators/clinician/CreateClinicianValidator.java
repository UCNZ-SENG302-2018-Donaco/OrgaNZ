package com.humanharvest.organz.utilities.validators.clinician;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;

/**
 * Class to ensure that all clinician's fields are valid
 */
public class CreateClinicianValidator {

    public static boolean isValid(Clinician clinician) {
        if (NotEmptyStringValidator.isInvalidString(clinician.getFirstName())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(clinician.getLastName())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(clinician.getWorkAddress())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(clinician.getPassword())) {
            return false;
        }

        return true;

    }
}
