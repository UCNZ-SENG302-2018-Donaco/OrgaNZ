package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.Region;
import picocli.CommandLine;

/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class RegionConverter implements CommandLine.ITypeConverter<Region> {

    /**
     * Convert a string to a Region, matches case insensitive
     * @param value String input from user via PicoCLI
     * @return Region object
     * @throws CommandLine.TypeConversionException Throws exception if invalid region
     */
    @Override
    public Region convert(String value) throws CommandLine.TypeConversionException {
        try {
            return Region.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}