package com.humanharvest.organz.utilities.exceptions;

/**
 * Thrown when the API returns a 500 level status code
 */
public class ServerRestException extends RuntimeException {

    public ServerRestException() {
        super();
    }

    public ServerRestException(String text) {
        super(text);
    }

}
