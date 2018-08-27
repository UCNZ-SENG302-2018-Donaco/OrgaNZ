package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.type_converters.OrganConverter;

import picocli.CommandLine.ITypeConverter;

public class PicoOrganConverter implements ITypeConverter<Organ> {

    @Override
    public Organ convert(String s) throws Exception {
        return new OrganConverter().convert(s);
    }
}
