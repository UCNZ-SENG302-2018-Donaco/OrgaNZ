package com.humanharvest.organz.server.exceptions;

public class IfMatchFailedException extends Exception {

    public IfMatchFailedException() {}

    public IfMatchFailedException(String text) {
        super(text);
    }

}
