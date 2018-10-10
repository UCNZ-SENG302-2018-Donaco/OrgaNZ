package com.humanharvest.organz.utilities.validators;

public abstract class NotEmptyStringValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private NotEmptyStringValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns a boolean which is true if the String is null or empty
     *
     * @param string The string to check
     * @return True if the string is null or empty
     */
    public static boolean isInvalidString(String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * Returns false if the String is null or empty
     *
     * @param string The string to check
     * @return false if the string is null or empty
     */
    public static boolean isValidString(String string) {
        return !isInvalidString(string);
    }
}
