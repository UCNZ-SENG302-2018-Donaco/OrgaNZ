package seng302.Utilities;

import picocli.CommandLine;

/**
 * Converter used by PicoCLI options to select bloodtypes from strings
 */
public class BloodTypeConverter implements CommandLine.ITypeConverter<BloodType> {

    @Override
    public BloodType convert(String value) throws CommandLine.TypeConversionException {
        try {
            return BloodType.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(value + " is not a valid blood type. Please enter in the form \"A-\"");
        }
    }
}
