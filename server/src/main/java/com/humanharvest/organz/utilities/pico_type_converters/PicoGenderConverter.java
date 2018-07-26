package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.type_converters.GenderConverter;
import com.humanharvest.organz.utilities.type_converters.TypeConversionException;
import picocli.CommandLine.ITypeConverter;

public class PicoGenderConverter implements ITypeConverter<Gender> {

    @Override
    public Gender convert(String value) throws TypeConversionException {
        return new GenderConverter().convert(value);
    }
}


