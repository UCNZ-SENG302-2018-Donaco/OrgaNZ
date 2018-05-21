package seng302.Utilities.TypeConverters;

import seng302.Utilities.Enums.Organ;

import picocli.CommandLine;
/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class OrganConverter implements CommandLine.ITypeConverter<Organ> {

    /**
     * Convert a string to an Organ, matches case insensitive
     * @param value String input from user via PicoCLI
     * @return Organ object
     * @throws CommandLine.TypeConversionException Throws exception if invalid organ
     */
    @Override
    public Organ convert(String value) throws CommandLine.TypeConversionException {
        try {
            return Organ.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(
                    value + " is not a valid organ. See the help text for organ request options.");
        }
    }
}
