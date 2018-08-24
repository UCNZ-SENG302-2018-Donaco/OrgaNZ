package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.ResolveReason;
import picocli.CommandLine;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolveReasonConverter implements CommandLine.ITypeConverter<ResolveReason> {

    private static final Logger LOGGER = Logger.getLogger(ResolveReasonConverter.class.getName());

    @Override
    public ResolveReason convert(String value) throws CommandLine.TypeConversionException {
        try {
            return ResolveReason.fromString(value);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }

}
