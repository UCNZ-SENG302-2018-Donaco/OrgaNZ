package com.humanharvest.organz.utilities.validators.client;

import com.humanharvest.organz.DonatedOrgan;

/**
 * Class to ensure that all DonatedOrgan fields are valid
 * Class is abstract as it only contains static methods and should not be instantiated
 */
public abstract class DonatedOrganValidator {

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private DonatedOrganValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check that none of the required DonatedOrgan fields are null
     *
     * @param donatedOrgan The object to check
     * @return True if all validity checks pass
     */
    public static boolean isValid(DonatedOrgan donatedOrgan) {
        return donatedOrgan != null
                && donatedOrgan.getOrganType() != null
                && donatedOrgan.getDonor() != null
                && donatedOrgan.getDateTimeOfDonation() != null
                && donatedOrgan.getId() != null;
    }
}
