package com.humanharvest.organz.utilities.validators.client;

import com.humanharvest.organz.DonatedOrgan;

public class DonatedOrganValidator {

    public static boolean isValid(DonatedOrgan donatedOrgan) {
        System.out.println(donatedOrgan);
        return donatedOrgan != null
                && donatedOrgan.getOrganType() != null
                && donatedOrgan.getDonor() != null
                && donatedOrgan.getDateTimeOfDonation() != null
                && donatedOrgan.getId() != null;
    }
}
