package com.humanharvest.organz.ui.validation;

public abstract class Validator {

    public abstract boolean isValid(Object value);

    public abstract String getErrorMessage();
}
