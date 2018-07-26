package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.ResolveReason;
import picocli.CommandLine;

public class ResolveReasonConverter implements CommandLine.ITypeConverter<ResolveReason> {

    @Override
    public ResolveReason convert(String value) throws CommandLine.TypeConversionException {
        try {
            return ResolveReason.fromString(value);
        } catch(IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }

}
