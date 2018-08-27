package com.humanharvest.organz.utilities.type_converters;

public interface TypeConverter<C> {

    C convert(Object value) throws Exception;
}