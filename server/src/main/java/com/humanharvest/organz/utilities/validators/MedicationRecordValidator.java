package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.MedicationRecord;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * A static MedicationRecord validator that checks integrity
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class MedicationRecordValidator {

    /**
     * Validates a {@link MedicationRecord} and returns a string explaining the errors within it.
     *
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public static String validate(MedicationRecord record) {
        StringBuilder errors = new StringBuilder();

        if (!medicationNameValid(record)) {
            errors.append("Medication name must not be empty.");
        }
        if (!startedDateValid(record)) {
            errors.append("Medication started date must be in a valid format and must represent a date in the past.\n");
        } else if (!stoppedDateValid(record)) {
            errors.append("Medication stopped date must be either empty, or a date in a valid format that represents a "
                    + "date after the started date.\n");
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private static boolean medicationNameValid(MedicationRecord record) {
        return record.getMedicationName() != null &&
                !record.getMedicationName().equals("");
    }

    private static boolean startedDateValid(MedicationRecord record) {
        return dateIsValid(record.getStarted()) &&
                !record.getStarted().isAfter(LocalDate.now());
    }

    private static boolean stoppedDateValid(MedicationRecord record) {
        if (record.getStopped() != null) {
            return dateIsValid(record.getStopped()) &&
                    !record.getStopped().isBefore(record.getStarted());
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
