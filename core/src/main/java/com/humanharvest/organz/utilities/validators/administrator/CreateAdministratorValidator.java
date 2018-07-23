package com.humanharvest.organz.utilities.validators.administrator;

import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;

/**
 * Class to ensure that all administrators's fields are valid
 */
public class CreateAdministratorValidator {

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
