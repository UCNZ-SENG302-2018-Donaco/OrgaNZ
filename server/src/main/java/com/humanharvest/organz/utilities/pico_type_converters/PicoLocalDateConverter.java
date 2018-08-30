package com.humanharvest.organz.utilities.pico_type_converters;

import java.time.LocalDate;

import com.humanharvest.organz.utilities.type_converters.LocalDateConverter;

import picocli.CommandLine.ITypeConverter;

public class PicoLocalDateConverter implements ITypeConverter<LocalDate> {

    @Override
    public LocalDate convert(String s) throws Exception {
        return new LocalDateConverter().convert(s);
    }
}
