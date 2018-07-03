package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.ResolveReason;

public class ResolveReasonConverter implements TypeConverter<ResolveReason> {

    @Override
    public ResolveReason convert(Object value) throws Exception {
        try {
            return ResolveReason.fromString(value.toString());
        } catch(IllegalArgumentException e) {
            throw new TypeConversionException(e.getMessage());
        }
    }
}
