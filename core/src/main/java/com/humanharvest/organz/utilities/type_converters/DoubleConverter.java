package com.humanharvest.organz.utilities.type_converters;

public class DoubleConverter implements TypeConverter<Double> {

    @Override
    public Double convert(Object value) throws Exception {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new TypeConversionException("Not a valid number");
    }
}
