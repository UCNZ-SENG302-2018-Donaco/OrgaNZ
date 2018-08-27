package com.humanharvest.organz.utilities.pico_type_converters;

import com.humanharvest.organz.utilities.enums.ResolveReason;
import com.humanharvest.organz.utilities.type_converters.ResolveReasonConverter;

import picocli.CommandLine.ITypeConverter;

public class PicoResolveReasonConverter implements ITypeConverter<ResolveReason> {

    @Override
    public ResolveReason convert(String s) throws Exception {
        return new ResolveReasonConverter().convert(s);
    }
}
