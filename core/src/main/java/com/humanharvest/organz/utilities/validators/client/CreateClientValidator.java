package com.humanharvest.organz.utilities.validators.client;

import java.time.LocalDate;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.client.CreateClientView;

/**
 * Class to ensure that a new client is valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class CreateClientValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private CreateClientValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks a CreateClientView for validity on the following:
     * First and Last names are not empty,
     * Date of birth is not in the future
     *
     * @param clientView The CreateClientView to check
     * @return True if all validity checks pass
     */
    public static boolean isValid(CreateClientView clientView) {
        //Check that the first and last names are supplied
        if (NotEmptyStringValidator.isInvalidString(clientView.getFirstName())) {
            return false;
        }
        if (NotEmptyStringValidator.isInvalidString(clientView.getLastName())) {
            return false;
        }
        //Check that the date of birth is not in the future
        if (clientView.getDateOfBirth().isAfter(LocalDate.now())) {
            return false;
        }
        return true;
    }
}
