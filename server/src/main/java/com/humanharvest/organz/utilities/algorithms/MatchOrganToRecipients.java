package com.humanharvest.organz.utilities.algorithms;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MatchOrganToRecipients {

    private static boolean agesMatch(int donorAge, int recipientAge) {
        // If one is under 12, they must both be under 12
        if (donorAge < 12 || recipientAge < 12) {
            return donorAge < 12 && recipientAge < 12;
        }

        // Otherwise (aged 12+), they must have a max age diff of 15
        return Math.abs(donorAge - recipientAge) <= 15;
    }

    public static List<Client> getListOfPotentialRecipients(DonatedOrgan donatedOrgan) {
        List<TransplantRequest> potentialTransplantRequests = new ArrayList<>();
        List<Client> potentialMatches = new ArrayList<>();

        // If the organ trying to be matched has expired, then return an empty list
        if (donatedOrgan.hasExpired()) {
            return potentialMatches;
        }

        // Create a list of eligible transplant requests
        for (TransplantRequest transplantRequest : State.getClientManager().getAllCurrentTransplantRequests()) {
            Client donor = donatedOrgan.getDonor();
            Client recipient = transplantRequest.getClient();

            if (donatedOrgan.getOrganType().equals(transplantRequest.getRequestedOrgan())
                    && donor.getBloodType().equals(recipient.getBloodType())
                    && agesMatch(donor.getAge(), recipient.getAge())) {
                potentialTransplantRequests.add(transplantRequest);
            }
        }

        // Sort the list by when the transplant request was made
        potentialTransplantRequests.sort((t1, t2) -> {
            LocalDateTime requestDate1 = t1.getRequestDate().truncatedTo(ChronoUnit.HOURS);
            LocalDateTime requestDate2 = t2.getRequestDate().truncatedTo(ChronoUnit.HOURS);
            int timeComparison = requestDate1.compareTo(requestDate2);

            if (timeComparison != 0) { // different time, so just compare using that
                return timeComparison;
            } else { // same(ish) time, so compare using location
                Client c1 = t1.getClient();
                Client c2 = t2.getClient();

                if (c1.getCountry().equals(c2.getCountry())) { // Same country, so can't use that

                    //todo check regions, then city
                } else { // Different countries
                    Country deathCountry = donatedOrgan.getDonor().getCountryOfDeath();
                    // Check if the one of the recipients is in the same country that the donor died
                    if (c1.getCountry() == deathCountry) {
                        return -1;
                    } else if (c2.getCountry() == deathCountry) {
                        return 1;
                    } else { // Neither is in the same country

                        return 0; //todo implement country location comparison

                    }

                }

            }
            return 0;
        });

        return potentialMatches;
    }

}
