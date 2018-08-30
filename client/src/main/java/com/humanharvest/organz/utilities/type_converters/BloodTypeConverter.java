package com.humanharvest.organz.utilities.type_converters;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.utilities.enums.BloodType;

import picocli.CommandLine;

/**
 * Converter used by PicoCLI options to select bloodtypes from strings
 */
public class BloodTypeConverter implements CommandLine.ITypeConverter<BloodType> {

    private static final Logger LOGGER = Logger.getLogger(BloodTypeConverter.class.getName());

    /**
     * Convert a string to a BloodType, matches case insensitive
     *
     * @param value String input from user via PicoCLI
     * @return BloodType object
     * @throws CommandLine.TypeConversionException Throws exception if invalid blood type
     */
    @Override
    public BloodType convert(String value) throws CommandLine.TypeConversionException {
        try {
            return BloodType.fromString(value);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}
