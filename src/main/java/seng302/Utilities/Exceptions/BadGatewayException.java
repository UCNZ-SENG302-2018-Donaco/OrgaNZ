package seng302.Utilities.Exceptions;

/**
 * Thrown when a web API responds with 502: Bad Gateway.
 */
public class BadGatewayException extends Exception {

    public BadGatewayException() {
        super();
    }

    public BadGatewayException(String message) {
        super(message);
    }
}
