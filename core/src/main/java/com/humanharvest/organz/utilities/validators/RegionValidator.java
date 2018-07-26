package com.humanharvest.organz.utilities.validators;

import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

public final class RegionValidator {

    private RegionValidator() {
    }

    public static boolean isValid(Country country, String region) {
        if (region != null && country == Country.NZ) {
            // Verify region is valid
            try {
                Region.fromString(region);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }
}
