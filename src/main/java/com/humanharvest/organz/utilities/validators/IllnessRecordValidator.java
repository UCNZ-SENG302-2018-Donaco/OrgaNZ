package com.humanharvest.organz.utilities.validators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.IllnessRecord;

public class IllnessRecordValidator {

    /**
     * Validates a {@link IllnessRecord} and returns a string explaining the errors within it.
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public String validate(IllnessRecord record) {
        StringBuilder errors = new StringBuilder();

        if (!illnessNameValid(record)) {
            errors.append("Illness name must not be empty.");
        }
        if (!startedDateValid(record)) {
            errors.append("Illness started date must be in a valid format and must represent a date in the past.\n");
        } else if (!stoppedDateValid(record)) {
            errors.append("Illness stopped date must be either empty, or a date in a valid format that represents a "
                    + "date after the started date.\n");
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private boolean illnessNameValid(IllnessRecord record) {
        return record.getIllnessName() != null &&
                !record.getIllnessName().equals("");
    }

    private boolean startedDateValid(IllnessRecord record) {
        return dateIsValid(record.getDiagnosisDate()) &&
                !record.getDiagnosisDate().isAfter(LocalDate.now());
    }

    private boolean stoppedDateValid(IllnessRecord record) {
        return dateIsValid(record.getCuredDate()) &&
                !record.getCuredDate().isBefore(record.getDiagnosisDate());
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
