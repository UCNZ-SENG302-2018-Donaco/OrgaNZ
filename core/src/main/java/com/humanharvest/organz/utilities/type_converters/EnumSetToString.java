package com.humanharvest.organz.utilities.type_converters;

import java.util.EnumSet;
import java.util.stream.Collectors;

public class EnumSetToString {

    public static String convert(EnumSet<?> set) {
        return set.stream().map(Enum::name).collect(Collectors.joining(","));
    }
}
