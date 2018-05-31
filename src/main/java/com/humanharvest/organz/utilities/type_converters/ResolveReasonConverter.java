package com.humanharvest.organz.utilities.type_converters;

import picocli.CommandLine;
import com.humanharvest.organz.utilities.enums.ResolveReason;

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
