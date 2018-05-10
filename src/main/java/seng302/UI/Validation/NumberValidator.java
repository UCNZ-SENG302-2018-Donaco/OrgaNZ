package seng302.UI.Validation;

import java.util.regex.Pattern;

public class NumberValidator extends Validator {
    private static final Pattern pattern = Pattern.compile("^\\d+$");

    @Override
    public boolean isValid(Object value) {
        if (value instanceof Integer) {
            return true;
        }
        if (value instanceof Float) {
            return true;
        }
        if (value instanceof CharSequence) {
            CharSequence sequence = (CharSequence) value;
            return pattern.matcher(sequence).matches();
        }
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "Not a valid number";
    }
}
