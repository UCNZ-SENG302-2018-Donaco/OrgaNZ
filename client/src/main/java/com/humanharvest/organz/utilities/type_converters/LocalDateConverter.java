package com.humanharvest.organz.utilities.type_converters;

import picocli.CommandLine;
import picocli.CommandLine.ITypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Converter used by PicoCLI options to select LocalDate from strings
 */
public class LocalDateConverter implements ITypeConverter<LocalDate> {

    /**
     * Convert a string to a LocalDate, must be form dd/mm/yyyy
     *
     * @param value String input from user via PicoCLI
     * @return LocalDate object
     * @throws CommandLine.TypeConversionException Throws exception if invalid date format
     */
    @Override
    public LocalDate convert(String value) throws CommandLine.TypeConversionException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new CommandLine.TypeConversionException("'" + value + "' is not a dd/mm/yyyy date");
        }
    }
}