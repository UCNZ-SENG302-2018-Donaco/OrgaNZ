package com.humanharvest.organz.utilities.exceptions;

/**
 * Thrown when a response has a 400 status code
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super();
    }

    public BadRequestException(String text) {
        super(text);
    }
}
