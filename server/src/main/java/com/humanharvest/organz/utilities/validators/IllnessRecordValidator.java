package com.humanharvest.organz.utilities.validators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.IllnessRecord;

/**
 * A static IllnessRecord validator that checks integrity
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class IllnessRecordValidator {

    /**
     * Validates a {@link IllnessRecord} and returns a string explaining the errors within it.
     *
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public static String validate(IllnessRecord record) {
        StringBuilder errors = new StringBuilder();

        if (!illnessNameValid(record)) {
            errors.append("Illness name must not be empty.");
        }
        if (!diagnosisDateValid(record)) {
            errors.append("Illness diagnosis date must be in a valid format and must represent a date in the past.\n");
        } else if (!curedDateValid(record)) {
            errors.append("Illness cured date must be either empty, or a date in a valid format that represents a "
                    + "date after the diagnosis date.\n");
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private static boolean illnessNameValid(IllnessRecord record) {
        return record.getIllnessName() != null &&
                !record.getIllnessName().equals("");
    }

    private static boolean diagnosisDateValid(IllnessRecord record) {
        return dateIsValid(record.getDiagnosisDate()) &&
                !record.getDiagnosisDate().isAfter(LocalDate.now());
    }

    private static boolean curedDateValid(IllnessRecord record) {
        if (record.getCuredDate() != null) {
            return dateIsValid(record.getCuredDate()) &&
                    !record.getCuredDate().isBefore(record.getDiagnosisDate());
        }
        return true;
    }

    // HELPERS

    private static boolean dateIsValid(LocalDate date) {
        // Catch any invalid dates (eg date >31), or dates with null months, etc
        try {
            LocalDate.parse(date.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
