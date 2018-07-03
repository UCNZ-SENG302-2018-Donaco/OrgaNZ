package com.humanharvest.organz.utilities.validators;

public class AcceptAnyValidator extends Validator {

    @Override
    public boolean isValid(Object value) {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return "";
    }
}
