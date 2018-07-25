package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.type_converters.GenderConverter;
import picocli.CommandLine.ITypeConverter;

public class PicoGenderConverter implements ITypeConverter<Gender> {

    @Override
    public Gender convert(String s) throws Exception {
        return new GenderConverter().convert(s);
    }
}
