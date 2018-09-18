package com.humanharvest.organz.utilities.type_converters;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class EnumSetToString {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private EnumSetToString() {
        throw new IllegalStateException("Utility class");
    }

    public static String convert(Collection<? extends Enum<?>> set) {
        return set
                .stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }
}
