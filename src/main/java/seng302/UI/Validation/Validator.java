package seng302.UI.Validation;

public abstract class Validator {

    public abstract boolean isValid(Object value);

    public abstract String getErrorMessage();
}
