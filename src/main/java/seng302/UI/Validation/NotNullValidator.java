package seng302.UI.Validation;

public class NotNullValidator extends Validator {
    @Override
    public boolean isValid(Object value) {
        return value != null;
    }

    @Override
    public String getErrorMessage() {
        return "Must not be empty.";
    }
}
