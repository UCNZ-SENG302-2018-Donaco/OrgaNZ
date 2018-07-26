package com.humanharvest.organz.utilities.type_converters;

public class TypeConversionException extends Exception {

    public TypeConversionException(Exception e) {
        super(e);
    }

    public TypeConversionException(String text) {
        super(text);
    }

    public TypeConversionException(String text, Exception e) {
        super(text, e);
    }
}
