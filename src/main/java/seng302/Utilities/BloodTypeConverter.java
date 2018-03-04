package seng302.Utilities;

import picocli.CommandLine;

public class BloodTypeConverter implements CommandLine.ITypeConverter<BloodType> {

    @Override
    public BloodType convert(String value) throws Exception {
        try {
            return BloodType.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(value + " is not a valid blood type. Please enter in the form \"A-\"");
        }
    }
}
