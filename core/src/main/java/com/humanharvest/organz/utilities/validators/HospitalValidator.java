package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.Hospital;

public final class HospitalValidator {

    private HospitalValidator() {
    }

    public static boolean isValid(Hospital hospital) {
        return !NotEmptyStringValidator.isInvalidString(hospital.getName())
                && !Double.isNaN(hospital.getLatitude())
                && !Double.isNaN(hospital.getLongitude())
                && !NotEmptyStringValidator.isInvalidString(hospital.getAddress())
                && !hospital.transplantProgramsIsNull();
    }

    public static boolean areValid(Iterable<Hospital> hospitals) {
        for (Hospital hospital : hospitals) {
            if (!isValid(hospital)) {
                return false;
            }
        }

        // They were all valid
        return true;
    }

}
