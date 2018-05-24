package com.humanharvest.organz.utilities.enums;

/**
 * Enum for regions. Allows for to/from string conversion
 */
public enum Region {
    NORTHLAND("Northland"),
    AUCKLAND("Auckland"),
    WAIKATO("Waikato"),
    BAY_OF_PLENTY("Bay of Plenty"),
    GISBORNE("Gisborne"),
    HAWKES_BAY("Hawkes Bay"),
    TARANAKI("Taranaki"),
    MANAWATU_WANGANUI("Manawatu-Wanganui"),
    WELLINGTON("Wellington"),
    TASMAN("Tasman"),
    NELSON("Nelson"),
    MARLBOROUGH("Marlborough"),
    WEST_COAST("West Coast"),
    CANTERBURY("Canterbury"),
    OTAGO("Otago"),
    SOUTHLAND("Southland"),
    UNSPECIFIED("Unspecified");

    private final String text;

    private static String mismatchText;

    Region(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }

    /**
     * Get a Region object from a string
     * @param text Text to convert
     * @return The matching region
     * @throws IllegalArgumentException Thrown when no matching region is found
     */
    public static Region fromString(String text) {
        for (Region r : Region.values()) {
            if (r.toString().equalsIgnoreCase(text)) {
                return r;
            }
        }

        //No match
        if (mismatchText != null) {
            throw new IllegalArgumentException(mismatchText);
        } else {
            StringBuilder mismatchTextBuilder = new StringBuilder("Unsupported region, please use one of the "
                    + "following:");
            for (Region r : Region.values()) {
                mismatchTextBuilder.append("\n").append(r.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }
}
