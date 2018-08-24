package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.Organ;
import picocli.CommandLine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class OrganConverter implements CommandLine.ITypeConverter<Organ> {

    private static final Logger LOGGER = Logger.getLogger(OrganConverter.class.getName());

    /**
     * Convert a string to an Organ, matches case insensitive
     *
     * @param value String input from user via PicoCLI
     * @return Organ object
     * @throws CommandLine.TypeConversionException Throws exception if invalid organ
     */
    @Override
    public Organ convert(String value) throws CommandLine.TypeConversionException {
        try {
            return Organ.fromString(value);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}
