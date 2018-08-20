package com.humanharvest.organz.utilities.type_converters;

import java.util.Collection;
import java.util.stream.Collectors;

public final class EnumSetToString {

    private EnumSetToString() {
    }

    public static String convert(Collection<? extends Enum<?>> set) {
        return set
            .stream()
            .map(Enum::name)
            .collect(Collectors.joining(","));
    }
}
