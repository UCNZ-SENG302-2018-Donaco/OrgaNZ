package seng302.Utilities;

import picocli.CommandLine;

/**
 * Converter used by PicoCLI options to select genders from strings
 */
public class GenderConverter implements CommandLine.ITypeConverter<Gender> {

    @Override
    public Gender convert(String value) {
        try {
            return Gender.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(value + " is not a supported gender. Please enter \"Male\", \"Female\", \"Other\" or \"Unspecified\"");
        }
    }
}
