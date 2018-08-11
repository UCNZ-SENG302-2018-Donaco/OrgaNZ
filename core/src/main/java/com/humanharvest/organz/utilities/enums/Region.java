package com.humanharvest.organz.utilities.enums;

/**
 * Enum for regions. Allows for to/from string conversion
 */
public enum Region {
    NORTHLAND("Northland", -35.58, 173.97),
    AUCKLAND("Auckland", -36.9, 174.78333333),
    WAIKATO("Waikato", -37.5, 175.33333333),
    BAY_OF_PLENTY("Bay of Plenty", -37.66666667, 177),
    GISBORNE("Gisborne", -38.65, 178),
    HAWKES_BAY("Hawke's Bay", -39.41666667, 176.81666667),
    TARANAKI("Taranaki", -39.3, 174.13333333),
    MANAWATU_WANGANUI("Manawatu-Wanganui", -39.6, 175.6),
    WELLINGTON("Wellington", -41.28333333, 174.76666667),
    TASMAN("Tasman", -41.5, 172.8),
    MARLBOROUGH("Marlborough", -41.88333333, 173.66666667),
    NELSON("Nelson", -41.27083333, 173.28388889),
    WEST_COAST("West Coast", -42.6, 171.4),
    CANTERBURY("Canterbury", -43.6, 172),
    OTAGO("Otago", -45.88333333, 170.5),
    SOUTHLAND("Southland", -45.7, 168.1),
    CHATHAM_ISLANDS("Chatham Islands", -44.03333333, -176.43333333),
    UNSPECIFIED("Unspecified", -200, -200);

    private final String text;
    private final double latitude;
    private final double longitude;

    private static String mismatchText;

    Region(String text, double latitude, double longitude) {
        this.text = text;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String toString() {
        return text;
    }

    /**
     * Get a Region object from a string
     *
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
            StringBuilder mismatchTextBuilder = new StringBuilder(
                    String.format("Unsupported region '%s', please use one of the following:", text));
            for (Region r : Region.values()) {
                mismatchTextBuilder.append('\n').append(r.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }
}
