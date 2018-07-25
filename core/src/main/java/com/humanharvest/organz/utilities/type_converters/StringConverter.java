package com.humanharvest.organz.utilities.type_converters;

public class StringConverter implements TypeConverter<String> {

    @Override
    public String convert(Object val) {
        return val.toString();
    }
}
