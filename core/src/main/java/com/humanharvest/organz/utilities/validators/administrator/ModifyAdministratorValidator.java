package com.humanharvest.organz.utilities.validators.administrator;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;

import java.util.Arrays;
import java.util.List;

public class ModifyAdministratorValidator {

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
