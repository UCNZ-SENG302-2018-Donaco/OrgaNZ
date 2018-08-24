package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class RegionValidator {

    private static final Logger LOGGER = Logger.getLogger(RegionValidator.class.getName());

    private RegionValidator() {
    }

    public static boolean isValid(Country country, String region) {
        if (region != null && country == Country.NZ) {
            // Verify region is valid
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
