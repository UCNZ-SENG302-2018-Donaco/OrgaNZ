package com.humanharvest.organz.utilities.validators;

public class NotEmptyStringValidator {

    public static boolean isInvalidString(String string) {
        return (string == null || string.trim().length() == 0);
    }

}
