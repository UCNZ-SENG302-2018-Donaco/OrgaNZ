package com.humanharvest.organz.utilities.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
    }

    public NotFoundException(String text) {
        super(text);
    }

    public NotFoundException(Exception e) {
        super(e);
    }

    public NotFoundException(String text, Exception e) {
        super(text, e);
    }
}
