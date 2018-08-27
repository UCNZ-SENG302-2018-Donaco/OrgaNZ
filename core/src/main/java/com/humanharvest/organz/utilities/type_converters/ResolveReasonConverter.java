package com.humanharvest.organz.utilities.type_converters;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.utilities.enums.ResolveReason;

public class ResolveReasonConverter implements TypeConverter<ResolveReason> {

    private static final Logger LOGGER = Logger.getLogger(ResolveReasonConverter.class.getName());

    @Override
    public ResolveReason convert(Object value) throws Exception {
        try {
            return ResolveReason.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new TypeConversionException(e.getMessage());
        }
    }
}
