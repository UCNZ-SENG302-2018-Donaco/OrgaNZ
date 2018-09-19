package com.humanharvest.organz.utilities.type_converters;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Defines static string formatting helper operations
 */
public abstract class StringFormatter {

    private static final Pattern CAMEL_CASE = Pattern.compile("([a-z])([A-Z]+)");

    /**
     * Prevent the class from being instantiated
     */
    private StringFormatter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert a camelCased string to proper english
     *
     * @param inCamelCase The camelCased string
     * @return The converted string
     */
    public static String unCamelCase(String inCamelCase) {
        String unCamelCased = CAMEL_CASE.matcher(inCamelCase).replaceAll("$1 $2");
        return unCamelCased.substring(0, 1).toUpperCase(Locale.UK) + unCamelCased.substring(1);
    }

}
