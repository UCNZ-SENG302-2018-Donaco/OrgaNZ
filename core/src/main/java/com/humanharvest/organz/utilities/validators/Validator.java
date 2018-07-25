package com.humanharvest.organz.utilities.validators;

public abstract class Validator {

    public abstract boolean isValid(Object value);

    public abstract String getErrorMessage();
}
