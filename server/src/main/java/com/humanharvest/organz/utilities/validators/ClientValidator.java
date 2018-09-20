package com.humanharvest.organz.utilities.validators;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;

/**
 * A static validator class used to check the integrity of a Client object
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class ClientValidator {

    private static final double DELTA = 1e-6;

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ClientValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates a {@link Client} and returns a string explaining the errors within it.
     *
     * @param client The client to validate.
     * @return A string containing the errors within the client if it is invalid, else null if the client is valid.
     */
    public static String validate(Client client) {
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
            String validationMsg = TransplantRequestValidator.validate(request);
            if (validationMsg != null) {
                errors.append(validationMsg);
            }
        }
        for (MedicationRecord record : client.getMedications()) {
            String validationMsg = MedicationRecordValidator.validate(record);
            if (validationMsg != null) {
                errors.append(validationMsg);
            }
        }
        for (IllnessRecord record : client.getIllnesses()) {
            String validationMsg = IllnessRecordValidator.validate(record);
            if (validationMsg != null) {
                errors.append(validationMsg);
            }
        }
        for (ProcedureRecord record : client.getProcedures()) {
            String validationMsg = ProcedureRecordValidator.validate(record);
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

    private static boolean uidValid(Client client) {
        return client.getUid() == null || client.getUid() > 0;
    }

    private static boolean firstNameValid(Client client) {
        return !NotEmptyStringValidator.isInvalidString(client.getFirstName());
    }

    private static boolean lastNameValid(Client client) {
        return !NotEmptyStringValidator.isInvalidString(client.getLastName());
    }

    private static boolean dateOfBirthValid(Client client) {
        return client.getDateOfBirth() != null &&
                dateIsValid(client.getDateOfBirth()) &&
                !client.getDateOfBirth().isAfter(LocalDate.now());  // Catch future date of birth
    }

    private static boolean dateOfDeathValid(Client client) {
        if (client.getDateOfDeath() != null) {
            // Catch date of death before date of birth
            return dateIsValid(client.getDateOfDeath()) &&
                    !client.getDateOfDeath().isBefore(client.getDateOfBirth());
        }
        return true;
    }

    private static boolean heightValid(Client client) {
        // Catch negative heights
        return client.getHeight() >= -DELTA;
    }

    private static boolean weightValid(Client client) {
        // Catch negative weights
        return client.getWeight() >= -DELTA;
    }

    private static boolean createdTimestampValid(Client client) {
        return client.getCreatedTimestamp() != null &&
                datetimeIsValid(client.getCreatedTimestamp()) &&
                !client.getCreatedTimestamp().isAfter(Instant.now());  // Catch future created timestamp
    }

    private static boolean modifiedTimestampValid(Client client) {
        if (client.getModifiedTimestamp() != null) {
            // Catch date of death before date of birth
            return datetimeIsValid(client.getModifiedTimestamp()) &&
                    !client.getModifiedTimestamp().isBefore(client.getCreatedTimestamp());
        }
        return true;
    }

    // HELPERS

    private static boolean dateIsValid(LocalDate date) {
        // Catch any invalid dates (eg date >31), or dates with null months, etc which can be created via Jackson
        try {
            LocalDate.parse(date.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }

    private static boolean datetimeIsValid(Instant datetime) {
        // Catch any invalid datetimes (eg date >31), or dates with null months, etc which can be created via Jackson
        try {
            Instant.parse(datetime.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
