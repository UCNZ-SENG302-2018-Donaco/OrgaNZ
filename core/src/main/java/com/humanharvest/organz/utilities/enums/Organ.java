package com.humanharvest.organz.utilities.enums;

import java.time.Duration;

/**
 * Enum for organs. Allows for to/from string conversion
 */
public enum Organ {
    BONE("Bone", Duration.ofDays(365 * 3), Duration.ofDays(365 * 10)),
    BONE_MARROW("Bone marrow", null, null),
    CORNEA("Cornea", Duration.ofDays(5), Duration.ofDays(7)),
    CONNECTIVE_TISSUE("Connective tissue", null, null),
    HEART("Heart", Duration.ofHours(4), Duration.ofHours(6)),
    INTESTINE("Intestine", Duration.ofHours(8), Duration.ofHours(12)),
    KIDNEY("Kidney", Duration.ofHours(48), Duration.ofHours(72)),
    LIVER("Liver", Duration.ofHours(24), Duration.ofHours(24)),
    LUNG("Lung", Duration.ofHours(4), Duration.ofHours(6)),
    MIDDLE_EAR("Middle ear", null, null),
    PANCREAS("Pancreas", Duration.ofHours(12), Duration.ofHours(24)),
    SKIN("Skin", Duration.ofDays(365 * 3), Duration.ofDays(365 * 10));

    private static String mismatchText;

    private final String text;
    private final Duration minExpiration;
    private final Duration maxExpiration;

    Organ(String text, Duration minExpiration, Duration maxExpiration) {
        this.text = text;
        this.minExpiration = minExpiration;
        this.maxExpiration = maxExpiration;
    }

    /**
     * Get an Organ object from a string
     *
     * @param text Text to convert
     * @return The matching organ
     * @throws IllegalArgumentException Thrown when no matching organ is found
     */
    public static Organ fromString(String text) {

        for (Organ o : Organ.values()) {
            if (o.toString().equalsIgnoreCase(text)) {
                return o;
            }
        }

        //No match
        //We use a static text builder so this text is dynamically built based on the ENUM options, but only needs to
        // be built once per runtime
        if (mismatchText != null) {
            throw new IllegalArgumentException(mismatchText);
        } else {
            StringBuilder mismatchTextBuilder = new StringBuilder("Unsupported organ, please use one of the "
                    + "following:");
            for (Organ o : Organ.values()) {
                mismatchTextBuilder.append('\n').append(o.text);
            }
            mismatchText = mismatchTextBuilder.toString();
            throw new IllegalArgumentException(mismatchText);
        }
    }

    @Override
    public String toString() {
        return text;
    }

    public Duration getMinExpiration() {
        return minExpiration;
    }

    public Duration getMaxExpiration() {
        return maxExpiration;
    }
}
