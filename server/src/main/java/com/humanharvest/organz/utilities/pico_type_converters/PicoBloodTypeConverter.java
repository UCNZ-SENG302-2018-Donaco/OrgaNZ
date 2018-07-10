package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.type_converters.BloodTypeConverter;
import picocli.CommandLine.ITypeConverter;

public class PicoBloodTypeConverter implements ITypeConverter<BloodType> {

    @Override
    public BloodType convert(String s) throws Exception {
        return new BloodTypeConverter().convert(s);
    }
}
