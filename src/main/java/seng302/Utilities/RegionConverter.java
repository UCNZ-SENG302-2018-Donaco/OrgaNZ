package seng302.Utilities;

import picocli.CommandLine;

/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class RegionConverter implements CommandLine.ITypeConverter<Region> {

    @Override
    public Region convert(String value) throws Exception {
        try {
            return Region.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(value + " is not a supported region. Please enter the region name");
        }
    }
}
