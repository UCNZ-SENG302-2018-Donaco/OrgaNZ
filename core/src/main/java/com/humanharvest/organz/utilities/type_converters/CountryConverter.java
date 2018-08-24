package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.Country;

/**
 * Converter used by PicoCLI options to select countries from strings
 */
public class CountryConverter implements TypeConverter<Country> {

    /**
     * Convert a string to a Country, matches case insensitive
     *
     * @param value String input from user via PicoCLI
     * @return Country object
     * @throws TypeConversionException Throws exception if country type
     */
    @Override
    public Country convert(Object value) throws TypeConversionException {
        if (value == null) {
            return null;
        }

        try {
            return Country.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            throw new TypeConversionException(e);
        }
    }
}
