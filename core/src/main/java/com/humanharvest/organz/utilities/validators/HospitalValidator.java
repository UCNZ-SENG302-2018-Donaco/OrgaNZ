package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.Hospital;

public final class HospitalValidator {

    private HospitalValidator() {
    }

    public static boolean isValid(Hospital hospital) {
        // latitude and longitude are valid if they're not (NaN, NaN).
        boolean validLatLong = !Double.isNaN(hospital.getLatitude()) && !Double.isNaN(hospital.getLongitude());
        return hospital.getName() != null
                && validLatLong
                && hospital.getAddress() != null
                && hospital.getTransplantPrograms() != null;
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
