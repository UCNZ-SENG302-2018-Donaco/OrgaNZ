package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.type_converters.CountryConverter;
import com.humanharvest.organz.utilities.type_converters.TypeConversionException;
import picocli.CommandLine.ITypeConverter;

public class PicoCountryConverter implements ITypeConverter<Country> {

    @Override
    public Country convert(String value) throws TypeConversionException {
        return new CountryConverter().convert(value);
    }
}
