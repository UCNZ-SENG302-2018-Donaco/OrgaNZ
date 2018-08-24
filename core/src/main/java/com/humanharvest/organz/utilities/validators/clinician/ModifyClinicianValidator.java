package com.humanharvest.organz.utilities.validators.clinician;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;

import java.util.Arrays;
import java.util.List;

public class ModifyClinicianValidator {

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
