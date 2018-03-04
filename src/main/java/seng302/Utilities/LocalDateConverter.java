package seng302.Util;

import picocli.CommandLine;
import picocli.CommandLine.ITypeConverter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateConverter implements ITypeConverter<LocalDate> {
    @Override
    public LocalDate convert(String value) throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(value, formatter);
        } catch (DateTimeParseException e) {
            throw new CommandLine.TypeConversionException("'" + value + "' is not a dd/mm/yyyy date");
        }
    }
}