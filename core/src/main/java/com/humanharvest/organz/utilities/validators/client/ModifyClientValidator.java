package com.humanharvest.organz.utilities.validators.client;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.utilities.validators.RegionValidator;
import com.humanharvest.organz.views.client.ModifyClientObject;

public final class ModifyClientValidator {

    private ModifyClientValidator() {
    }

    public static boolean isValid(Client client, ModifyClientObject clientView) {
        // Get a list of unmodified fields so we don't check fields that haven't changed
        List<String> unmodifiedFields = Arrays.asList(clientView.getUnmodifiedFields());

        // Check that the first and last names aren't being set to null or empty strings
        if (!unmodifiedFields.contains("firstName") &&
                NotEmptyStringValidator.isInvalidString(clientView.getFirstName())) {
            return false;
        }
        if (!unmodifiedFields.contains("lastName") &&
                NotEmptyStringValidator.isInvalidString(clientView.getLastName())) {
            return false;
        }

        // Check that the dates are not in the future
        // Also check that DOB is not null, and if DOD is null, not to check it (to avoid NPEs)
        if (!unmodifiedFields.contains("dateOfBirth") &&
                (clientView.getDateOfBirth() == null || clientView.getDateOfBirth().isAfter(LocalDate.now()))) {
            return false;
        }
        if (!unmodifiedFields.contains("dateOfDeath") &&
                clientView.getDateOfDeath() != null && clientView.getDateOfDeath().isAfter(LocalDate.now())) {
            return false;
        }

        // If both dates have been modified, check they are not inconsistent. If only one or the other then it will
        // need to be checked against the client object later
        if (!unmodifiedFields.contains("dateOfDeath") && !unmodifiedFields.contains("dateOfBirth")) {
            if (clientView.getDateOfBirth().isAfter(clientView.getDateOfDeath())) {
                //Birth is after death, is invalid
                return false;
            }
        }

        if (!unmodifiedFields.contains("region")) {
            Country country;
            if (unmodifiedFields.contains("country")) {
                country = client.getCountry();
            } else {
                country = clientView.getCountry();
            }

            if (!RegionValidator.isValid(country, clientView.getRegion())) {
                return false;
            }
        }

        return true;
    }

}