package com.humanharvest.organz.utilities.validators;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;

public class ClientValidator {

    private static final double DELTA = 1e-6;

    private final TransplantRequestValidator transplantRequestValidator = new TransplantRequestValidator();
    private final MedicationRecordValidator medicationRecordValidator = new MedicationRecordValidator();

    /**
     * Validates a {@link Client} and returns a string explaining the errors within it.
     * @param client The client to validate.
     * @return A string containing the errors within the client if it is invalid, else null if the client is valid.
     */
    public String validate(Client client) {
        StringBuilder errors = new StringBuilder();

        if (!uidValid(client)) {
            errors.append("UID must be an integer greater than 0.\n");
        }
        if (!firstNameValid(client)) {
            errors.append("First name must not be empty.\n");
        }
        if (!lastNameValid(client)) {
            errors.append("Last name must not be empty.\n");
        }
        if (!dateOfBirthValid(client)) {
            errors.append("Date of birth must be in a valid format and must represent a date in the past.\n");
        } else if (!dateOfDeathValid(client)) {
            errors.append("Date of death must be either empty, or a date in a valid format that represents a date "
                    + "after the date of birth.\n");
        }
        if (!heightValid(client)) {
            errors.append("Height must be a non-negative number.\n");
        }
        if (!weightValid(client)) {
            errors.append("Weight must be a non-negative number.\n");
        }
        if (!createdTimestampValid(client)) {
            errors.append("Created timestamp must be in a valid format and must represent a date in the past.\n");
        } else if (!modifiedTimestampValid(client)) {
            errors.append("Modified timestamp must be either empty, or a datetime in a valid format that represents "
                    + "a time after the profile was created.\n");
        }

        for (TransplantRequest request : client.getTransplantRequests()) {
            String validationMsg = transplantRequestValidator.validate(request);
            if (validationMsg != null) {
                errors.append(validationMsg);
            }
        }

        for (MedicationRecord record : client.getMedications()) {
            String validationMsg = medicationRecordValidator.validate(record);
            if (validationMsg != null) {
                errors.append(validationMsg);
            }
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private boolean uidValid(Client client) {
        return client.getUid() == null || client.getUid() > 0;
    }

    private boolean firstNameValid(Client client) {
        return client.getFirstName() != null && !client.getFirstName().equals("");
    }

    private boolean lastNameValid(Client client) {
        return client.getLastName() != null && !client.getLastName().equals("");
    }

    private boolean dateOfBirthValid(Client client) {
        return client.getDateOfBirth() != null &&
                dateIsValid(client.getDateOfBirth()) &&
                !client.getDateOfBirth().isAfter(LocalDate.now());  // Catch future date of birth
    }

    private boolean dateOfDeathValid(Client client) {
        if (client.getDateOfDeath() != null) {
            // Catch date of death before date of birth
            return dateIsValid(client.getDateOfDeath()) &&
                    !client.getDateOfDeath().isBefore(client.getDateOfBirth());
        }
        return true;
    }

    private boolean heightValid(Client client) {
        // Catch negative heights
        return client.getHeight() >= -DELTA;
    }

    private boolean weightValid(Client client) {
        // Catch negative weights
        return client.getWeight() >= -DELTA;
    }

    private boolean createdTimestampValid(Client client) {
        return client.getCreatedTimestamp() != null &&
                datetimeIsValid(client.getCreatedTimestamp()) &&
                !client.getCreatedTimestamp().isAfter(LocalDateTime.now());  // Catch future created timestamp
    }

    private boolean modifiedTimestampValid(Client client) {
        if (client.getModifiedTimestamp() != null) {
            // Catch date of death before date of birth
            return datetimeIsValid(client.getModifiedTimestamp()) &&
                    !client.getModifiedTimestamp().isBefore(client.getCreatedTimestamp());
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

    private boolean datetimeIsValid(LocalDateTime datetime) {
        try {
            LocalDateTime.parse(datetime.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
