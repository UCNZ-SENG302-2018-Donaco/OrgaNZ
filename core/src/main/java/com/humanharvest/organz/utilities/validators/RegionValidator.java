package com.humanharvest.organz.utilities.validators;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

public abstract class RegionValidator {

    private static final Logger LOGGER = Logger.getLogger(RegionValidator.class.getName());

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private RegionValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValid(Country country, String region) {
        if (NotEmptyStringValidator.isInvalidString(region)) {
            // empty region is valid
            return true;
        } else if (country == Country.NZ) {
            // Verify region is a valid NZ region
            try {
                Region.fromString(region);
            } catch (IllegalArgumentException e) {
                // not a valid region
                LOGGER.log(Level.INFO, "not a valid region", e);
                return false;
            }
        }
        return true;
    }
}
