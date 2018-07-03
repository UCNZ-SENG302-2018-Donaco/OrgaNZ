package com.humanharvest.organz.utilities.type_converters;

import com.humanharvest.organz.utilities.enums.Region;

/**
 * Converter used by PicoCLI options to select regions from strings
 */
public class RegionConverter implements TypeConverter<Region> {

    /**
     * Convert a string to a Region, matches case insensitive
     * @param value String input from user via PicoCLI
     * @return Region object
     * @throws TypeConversionException Throws exception if invalid region
     */
    @Override
    public Region convert(Object value) throws TypeConversionException {
        try {
            return Region.fromString(value.toString());
        } catch (IllegalArgumentException e) {
            throw new TypeConversionException(e.getMessage());
        }
    }
}