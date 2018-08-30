package com.humanharvest.organz.utilities.validators;

import java.util.Optional;

public class StringValidator extends Validator {

    protected static Optional<String> getAsString(Object value) {
        if (value == null) {
            return Optional.empty();
        }

        String stringValue = (String) value;
        if (stringValue.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(stringValue);
    }

    @Override
    public boolean isValid(Object value) {
        return getAsString(value).isPresent();
    }

    @Override
    public String getErrorMessage() {
        return "Not a valid string";
    }
}

