package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.ProcedureRecord;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ProcedureRecordValidator {

    /**
     * Validates a {@link ProcedureRecord} and returns a string explaining the errors within it.
     *
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public String validate(ProcedureRecord record) {
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

    private boolean summaryValid(ProcedureRecord record) {
        return record.getSummary() != null &&
                !record.getSummary().equals("");
    }

    private boolean dateValid(ProcedureRecord record) {
        return dateIsValid(record.getDate()) &&
                !record.getDate().isAfter(LocalDate.now());
    }

    // HELPERS

    private boolean dateIsValid(LocalDate date) {
        // Catch any invalid dates (eg date >31), or dates with null months, etc
        try {
            LocalDate.parse(date.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
