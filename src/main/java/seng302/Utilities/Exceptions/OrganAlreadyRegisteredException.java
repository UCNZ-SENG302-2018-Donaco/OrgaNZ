package seng302.Utilities.Exceptions;

/**
 * Used to throw an exception when a user tries to register an organ that they have already registered
 */
public class OrganAlreadyRegisteredException extends Exception {

    public OrganAlreadyRegisteredException(String text) {
        super(text);
    }
}
