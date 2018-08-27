package com.humanharvest.organz.utilities.validators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.ProcedureRecord;

/**
 * A static ProcedureRecord validator that checks integrity
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class ProcedureRecordValidator {

    /**
     * Validates a {@link ProcedureRecord} and returns a string explaining the errors within it.
     *
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public static String validate(ProcedureRecord record) {
        StringBuilder errors = new StringBuilder();

        if (!summaryValid(record)) {
            errors.append("Procedure summary must not be empty.");
        }
        if (!dateValid(record)) {
            errors.append(
                    "Procedure diagnosis date must be in a valid format and must represent a date in the past.\n");
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private static boolean summaryValid(ProcedureRecord record) {
        return record.getSummary() != null &&
                !record.getSummary().equals("");
    }

    private static boolean dateValid(ProcedureRecord record) {
        return dateIsValid(record.getDate()) &&
                !record.getDate().isAfter(LocalDate.now());
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
