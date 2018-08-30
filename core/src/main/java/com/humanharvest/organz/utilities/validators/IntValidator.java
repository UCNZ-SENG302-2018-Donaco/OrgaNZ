package com.humanharvest.organz.utilities.validators;

import java.util.OptionalInt;
import java.util.regex.Pattern;

public class IntValidator extends Validator {

    private static final Pattern pattern = Pattern.compile("^\\d+$");

    protected static OptionalInt getAsInt(Object value) {
        if (value instanceof Integer) {
            return OptionalInt.of((Integer) value);
        }
        if (value instanceof String) {
            String sequence = (String) value;
            if (!pattern.matcher(sequence).matches()) {
                return OptionalInt.empty();
            }

            try {
                return OptionalInt.of(Integer.parseInt(sequence));
            } catch (NumberFormatException ignored) {
                return OptionalInt.empty();
            }
        }

        return OptionalInt.empty();
    }

    @Override
    public boolean isValid(Object value) {
        return getAsInt(value).isPresent();
    }

    @Override
    public String getErrorMessage() {
        return "Not a valid number";
    }
}

