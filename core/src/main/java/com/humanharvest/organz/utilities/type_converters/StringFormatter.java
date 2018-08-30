package com.humanharvest.organz.utilities.type_converters;

/**
 * Defines static string formatting helper operations
 */
public abstract class StringFormatter {

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
        String unCamelCased = inCamelCase.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        return unCamelCased.substring(0, 1).toUpperCase() + unCamelCased.substring(1);
    }

}
