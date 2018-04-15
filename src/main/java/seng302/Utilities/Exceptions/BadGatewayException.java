package seng302.Utilities.Exceptions;

/**
 * Used to throw an exception when a server responds with 502: Bad Gateway.
 */
public class BadGatewayException extends Exception {

    public BadGatewayException(String text) {
        super(text);
    }

}
