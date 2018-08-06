package com.humanharvest.organz.utilities.enums;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Enum for organs. Allows for to/from string conversion
 */
public enum Organ {
    LIVER("Liver", Duration.ofHours(24), Duration.ofHours(24)),
    KIDNEY("Kidney", Duration.ofHours(48), Duration.ofHours(72)),
    PANCREAS("Pancreas", Duration.ofHours(12), Duration.ofHours(24)),
    HEART("Heart", Duration.ofHours(4), Duration.ofHours(6)),
    LUNG("Lung", Duration.ofHours(4), Duration.ofHours(6)),
    INTESTINE("Intestine", null, null),  // TODO find data
    CORNEA("Cornea", Duration.ofDays(5), Duration.ofDays(7)),
    MIDDLE_EAR("Middle ear", null, null),  // TODO find data
    SKIN("Skin", Duration.ofDays(365 * 3), Duration.ofDays(365 * 10)),
    BONE("Bone", Duration.ofDays(365 * 3), Duration.ofDays(365 * 10)),
    BONE_MARROW("Bone marrow", null, null),  // TODO find data
    CONNECTIVE_TISSUE("Connective tissue", null, null);  // TODO find data

    // Placeholder expiration durations for organs where no reliable data can be found
    private static final Duration defaultMinExpiration = Duration.ofHours(12);
    private static final Duration defaultMaxExpiration = Duration.ofHours(24);

    private final String text;
    private final Duration minExpiration;
    private final Duration maxExpiration;

    private static String mismatchText;

    Organ(String text, Duration minExpiration, Duration maxExpiration) {
        this.text = text;
        this.minExpiration = minExpiration;
        this.maxExpiration = maxExpiration;
    }

    @Override
    public String toString() {
        return text;
    }

    public Duration getMinExpiration() {
        return minExpiration == null ? defaultMinExpiration : minExpiration;
    }

    public Duration getMaxExpiration() {
        return maxExpiration == null ? defaultMaxExpiration : maxExpiration;
    }

    /**
     * Get an Organ object from a string
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
}
