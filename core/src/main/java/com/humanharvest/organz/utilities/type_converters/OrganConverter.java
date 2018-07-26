package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.Organ;

/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class OrganConverter implements TypeConverter<Organ> {

    /**
     * Convert a string to an Organ, matches case insensitive
     * @param value String input from user via PicoCLI
     * @return Organ object
     * @throws TypeConversionException Throws exception if invalid organ
     */
    @Override
    public Organ convert(Object value) throws TypeConversionException {
        try {
            return Organ.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            throw new TypeConversionException(e.getMessage());
        }
    }
}
