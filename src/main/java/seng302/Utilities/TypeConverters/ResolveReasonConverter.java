package seng302.Utilities.TypeConverters;

import picocli.CommandLine;
import seng302.Utilities.Enums.ResolveReason;

public class ResolveReasonConverter implements CommandLine.ITypeConverter<ResolveReason> {

    @Override
    public ResolveReason convert(String value) throws CommandLine.TypeConversionException {
        try {
            return ResolveReason.fromString(value);
        } catch(IllegalArgumentException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }

}
