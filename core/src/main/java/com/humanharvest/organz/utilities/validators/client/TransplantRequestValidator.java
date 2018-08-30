package com.humanharvest.organz.utilities.validators.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.humanharvest.organz.TransplantRequest;

/**
 * A static TransplantRequest validator that checks integrity used in importing
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
     * Given a TransplantRequest, check that it matches
     * A valid organ,
     * Valid request date that is not in the future
     * Valid status
     * If the request is resolved, that the resolution is not before the request
     *
     * If any of these fail, an IllegalArgumentException will be thrown
     *
     * @param request The TransplantRequest to check
     */
    public static void validateTransplantRequest(TransplantRequest request) {
        int clientId = request.getClient().getUid();

        // Check for valid organ
        if (request.getRequestedOrgan() == null) {
            throw new IllegalArgumentException("Not a valid clients file: "
                    + "all organs being requested should be valid organs.\n"
                    + "Currently, user " + clientId + " has at least one that isn't.");
        }

        // Check for valid request date
        try {
            LocalDateTime.parse(request.getRequestDate().toString());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Not a valid clients file: "
                    + "all transplant requests should have valid dates.\n"
                    + "Currently, user " + clientId + " has at least one that isn't.");
        }

        // Catch any future request dates
        if (request.getRequestDate().isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new IllegalArgumentException("Not a valid clients file: "
                    + "all transplant requests should have dates in the past.\n"
                    + "Currently, user " + clientId + " has at least one that isn't.");
        }

        // Check for valid request status
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Not a valid clients file: "
                    + "all transplant requests should have a valid status.\n"
                    + "Currently, user " + clientId + " has at least one that doesn't.");
        }

        if (request.getResolvedDate() != null) {
            // Check for valid resolve date
            try {
                LocalDateTime.parse(request.getResolvedDate().toString());
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Not a valid clients file: "
                        + "all transplant requests should have valid resolution dates (if they have a"
                        + " resolution date).\n"
                        + "Currently, user " + clientId + " has at least one that isn't.");
            }

            // Catch any future resolve dates
            if (request.getResolvedDate().isAfter(LocalDateTime.now().plusMinutes(1))) {
                throw new IllegalArgumentException("Not a valid clients file: "
                        + "all transplant requests should have resolution dates in the past "
                        + "(if they have a resolution date).\n"
                        + "Currently, user " + clientId + " has at least one that isn't.");
            }

            // Catch any resolve dates before the request date
            if (request.getResolvedDate().isBefore(request.getRequestDate())) {
                throw new IllegalArgumentException("Not a valid clients file: "
                        + "all transplant requests should have resolution dates "
                        + "after the request date (if they have a resolution date).\n"
                        + "Currently, user " + clientId + " has at least one that isn't.");
            }
        }
    }

}
