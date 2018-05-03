package seng302.Utilities.Exceptions;

/**
 * Thrown when a web API responds with a 'bad drug name' HTTP response.
 */
public class BadDrugNameException extends Exception {

    public BadDrugNameException() {
        super();
    }

    public BadDrugNameException(String message) {
        super(message);
    }
}
