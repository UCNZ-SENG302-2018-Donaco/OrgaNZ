package com.humanharvest.organz.utilities.validators.client;

import java.time.LocalDate;

import com.humanharvest.organz.Views.Client.CreateClientView;
import com.humanharvest.organz.utilities.validators.NotEmptyStringValidator;

public class CreateClientValidator {

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
