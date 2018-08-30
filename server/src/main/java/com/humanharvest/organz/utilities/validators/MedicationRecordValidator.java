package com.humanharvest.organz.utilities.validators;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.MedicationRecord;

public class MedicationRecordValidator {

    /**
     * Validates a {@link MedicationRecord} and returns a string explaining the errors within it.
     * @param record The record to validate.
     * @return A string containing the errors within the record if it is invalid, else null if it is valid.
     */
    public String validate(MedicationRecord record) {
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

    private boolean medicationNameValid(MedicationRecord record) {
        return record.getMedicationName() != null &&
                !"".equals(record.getMedicationName());
    }

    private boolean startedDateValid(MedicationRecord record) {
        return dateIsValid(record.getStarted()) &&
                !record.getStarted().isAfter(LocalDate.now());
    }

    private boolean stoppedDateValid(MedicationRecord record) {
        if (record.getStopped() != null) {
            return dateIsValid(record.getStopped()) &&
                    !record.getStopped().isBefore(record.getStarted());
        }
        return true;
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
