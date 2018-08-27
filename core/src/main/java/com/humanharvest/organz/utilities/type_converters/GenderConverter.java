package com.humanharvest.organz.utilities.type_converters;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.utilities.enums.Gender;

/**
 * Converter used by PicoCLI options to select genders from strings
 */
public class GenderConverter implements TypeConverter<Gender> {

    private static final Logger LOGGER = Logger.getLogger(GenderConverter.class.getName());

    /**
     * Convert a string to a Gender, matches case insensitive
     *
     * @param value String input from user via PicoCLI
     * @return Gender object
     * @throws TypeConversionException Throws exception if gender type
     */
    @Override
    public Gender convert(Object value) throws TypeConversionException {
        try {
            return Gender.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new TypeConversionException(e);
        }
    }
}

