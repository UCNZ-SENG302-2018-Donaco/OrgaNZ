package seng302;

import picocli.CommandLine;

public class GenderConverter implements CommandLine.ITypeConverter<Gender> {

    @Override
    public Gender convert(String value) throws Exception {
        try {
            return Gender.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(value + " is not a supported gender. Please enter \"Male\", \"Female\", \"Other\" or \"Unspecified\"");
        }
    }
}
