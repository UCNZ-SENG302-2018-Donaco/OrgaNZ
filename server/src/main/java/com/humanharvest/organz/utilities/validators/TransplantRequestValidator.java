package com.humanharvest.organz.utilities.validators;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

/**
 * A static TransplantRequest validator that checks integrity
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class TransplantRequestValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private TransplantRequestValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates a {@link TransplantRequest} and returns a string explaining the errors within it.
     *
     * @param request The request to validate.
     * @return A string containing the errors within the request if it is invalid, else null if it is valid.
     */
    public static String validate(TransplantRequest request) {
        StringBuilder errors = new StringBuilder();

        if (!requestedOrganValid(request)) {
            errors.append(String.format("Requested organ must be one of these recognised organs: %s%n",
                    Arrays.stream(Organ.values())
                            .map(Organ::toString)
                            .collect(Collectors.joining(", "))
            ));
        }
        if (!requestDateTimeValid(request)) {
            errors.append("Request datetime must be in a valid format and must represent a point in the past.\n");
        } else if (!resolvedDateTimeValid(request)) {
            errors.append("Resolved date/time must be either empty, or a datetime in a valid format that represents a "
                    + "point after the request date.\n");
        }
        if (!statusValid(request)) {
            errors.append(String.format("Request status must be one of these values: %s%n",
                    Arrays.stream(TransplantRequestStatus.values())
                            .map(TransplantRequestStatus::toString)
                            .collect(Collectors.joining(", "))
            ));
        }

        if (errors.length() == 0) {
            return null;
        } else {
            return errors.toString();
        }
    }

    // FIELD VALIDATORS

    private static boolean requestedOrganValid(TransplantRequest request) {
        return request.getRequestedOrgan() != null;
    }

    private static boolean requestDateTimeValid(TransplantRequest request) {
        return dateTimeIsValid(request.getRequestDateTime()) &&
                !request.getRequestDateTime().isAfter(LocalDateTime.now().plusMinutes(1));
    }

    private static boolean resolvedDateTimeValid(TransplantRequest request) {
        if (request.getResolvedDateTime() != null) {
            return dateTimeIsValid(request.getResolvedDateTime()) &&
                    !request.getResolvedDateTime().isBefore(request.getRequestDateTime());
        }
        return true;
    }

    private static boolean statusValid(TransplantRequest request) {
        return request.getStatus() != null;
    }

    // HELPERS

    private static boolean dateTimeIsValid(LocalDateTime datetime) {
        try {
            LocalDateTime.parse(datetime.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
