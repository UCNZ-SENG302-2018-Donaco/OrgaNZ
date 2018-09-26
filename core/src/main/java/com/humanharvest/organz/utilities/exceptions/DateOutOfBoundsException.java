package com.humanharvest.organz.utilities.exceptions;

/**
 * Used to indicate a situation where the current date is incompatible with the requested action
 */
public class DateOutOfBoundsException extends Exception {

    public DateOutOfBoundsException(String string) {
        super(string);
    }
}
