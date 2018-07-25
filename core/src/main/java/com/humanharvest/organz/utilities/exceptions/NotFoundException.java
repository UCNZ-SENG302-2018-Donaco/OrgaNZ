package com.humanharvest.organz.utilities.exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String text) {
        super(text);
    }

}
