package com.humanharvest.organz.utilities.validators;

import java.util.Set;

import com.humanharvest.organz.Hospital;

public abstract class HospitalValidator {

    public static boolean isValid(Hospital hospital) {
        // latitude and longitude are valid if they're not (0, 0) - this is a point way off the coast of Africa, so is
        // unlikely to be the location of a hospital. If a hospital is created via Jackson without latitude and
        // longitude fields, it will set them both to 0.
        boolean validLatLong = hospital.getLatitude() != 0 && hospital.getLongitude() != 0;
        System.out.print("valid lat long: ");
        System.out.println(validLatLong);
        return hospital.getName() != null
                && validLatLong
                && hospital.getAddress() != null
                && hospital.getTransplantPrograms() != null;
    }

    public static boolean areValid(Set<Hospital> hospitals) {
        for (Hospital hospital : hospitals) {
            System.out.println(hospital);
            System.out.println(hospital.getName());
            System.out.println(hospital.getAddress());
            System.out.println(hospital.getTransplantPrograms());
            if (!isValid(hospital)) {
                System.out.println("that was invalid");
                return false;
            }
        }

        // They were all valid
        System.out.println("all valid");
        return true;
    }

}
