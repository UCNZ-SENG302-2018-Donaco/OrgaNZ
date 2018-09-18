package com.humanharvest.organz.utilities.validators.administrator;

import java.util.Arrays;
import java.util.List;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;

/**
 * Class to ensure that all administrators's fields are valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class ModifyAdministratorValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private ModifyAdministratorValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Ensures that the given ModifyAdministratorObject has valid:
     * Non empty strings for username and password
     *
     * @param administratorView The Object to check
     * @return True if both strings are valid
     */
    public static boolean isValid(ModifyAdministratorObject administratorView) {
        //Get a list of unmodified fields so we don't check fields that haven't changed
        List<String> unmodifiedFields = Arrays.asList(administratorView.getUnmodifiedFields());

        //Check that the first and last names aren't being set to null or empty strings
        if (!unmodifiedFields.contains("username") &&
                NotEmptyStringValidator.isInvalidString(administratorView.getUsername())) {
            return false;
        }

        if (!unmodifiedFields.contains("password") &&
                NotEmptyStringValidator.isInvalidString(administratorView.getPassword())) {
            return false;
        }

        return true;
    }

}
