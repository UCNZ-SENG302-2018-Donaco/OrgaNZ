package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TransplantRequestValidator {

    /**
     * Validates a {@link TransplantRequest} and returns a string explaining the errors within it.
     *
     * @param request The request to validate.
     * @return A string containing the errors within the request if it is invalid, else null if it is valid.
     */
    public String validate(TransplantRequest request) {
        StringBuilder errors = new StringBuilder();

        if (!requestedOrganValid(request)) {
            errors.append(String.format("Requested organ must be one of these recognised organs: %s\n",
                    Arrays.stream(Organ.values())
                            .map(Organ::toString)
                            .collect(Collectors.joining(", "))
            ));
        }
        if (!requestDateValid(request)) {
            errors.append("Request datetime must be in a valid format and must represent a point in the past.\n");
        } else if (!resolvedDateValid(request)) {
            errors.append("Resolved date/time must be either empty, or a datetime in a valid format that represents a "
                    + "point after the request date.\n");
        }
        if (!statusValid(request)) {
            errors.append(String.format("Request status must be one of these values: %s\n",
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

    private boolean requestedOrganValid(TransplantRequest request) {
        return request.getRequestedOrgan() != null;
    }

    private boolean requestDateValid(TransplantRequest request) {
        return datetimeIsValid(request.getRequestDate()) &&
                !request.getRequestDate().isAfter(LocalDateTime.now().plusMinutes(1));
    }

    private boolean resolvedDateValid(TransplantRequest request) {
        if (request.getResolvedDate() != null) {
            return datetimeIsValid(request.getResolvedDate()) &&
                    !request.getResolvedDate().isBefore(request.getRequestDate());
        }
        return true;
    }

    private boolean statusValid(TransplantRequest request) {
        return request.getStatus() != null;
    }

    // HELPERS

    private boolean datetimeIsValid(LocalDateTime datetime) {
        try {
            LocalDateTime.parse(datetime.toString());
            return true;
        } catch (DateTimeParseException exc) {
            return false;
        }
    }
}
