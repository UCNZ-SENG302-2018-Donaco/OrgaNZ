package com.humanharvest.organz.utilities.validators.administrator;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;

/**
 * Class to ensure that all administrators's fields are valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class CreateAdministratorValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private CreateAdministratorValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Ensures that the given CreateAdministratorView has valid:
     * Non empty strings for username and password
     *
     * @param administratorView The View to check
     * @return True if both strings are valid
     */
    public static boolean isValid(CreateAdministratorView administratorView) {
        if (NotEmptyStringValidator.isInvalidString(administratorView.getUsername())) {
            return false;
        }

        if (NotEmptyStringValidator.isInvalidString(administratorView.getPassword())) {
            return false;
        }

        return true;
    }
}
