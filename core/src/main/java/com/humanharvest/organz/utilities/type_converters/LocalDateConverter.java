package com.humanharvest.organz.utilities.type_converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converter used by PicoCLI options to select LocalDate from strings
 */
public class LocalDateConverter implements TypeConverter<LocalDate> {

    /**
     * Convert a string to a LocalDate, must be form dd/mm/yyyy
     *
     * @param value String input from user via PicoCLI
     * @return LocalDate object
     * @throws TypeConversionException Throws exception if invalid date format
     */
    @Override
    public LocalDate convert(Object value) throws TypeConversionException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(value.toString(), formatter);
        } catch (DateTimeParseException e) {
            throw new TypeConversionException("'" + value + "' is not a dd/mm/yyyy date");
        }
    }
}