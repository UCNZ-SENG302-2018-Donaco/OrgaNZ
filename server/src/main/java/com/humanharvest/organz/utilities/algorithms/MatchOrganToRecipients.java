package com.humanharvest.organz.utilities.algorithms;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

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

    private static double distanceBetween(Country country1, Country country2) {
        double latitudeDelta = Math.abs(country1.getLatitude() - country2.getLatitude());
        double longitudeDelta = Math.abs(country1.getLongitude() - country2.getLongitude());
        // Using trigonometry, calculate distance = sqrt(a^2 + b^2)
        return Math.sqrt(latitudeDelta * latitudeDelta + longitudeDelta * longitudeDelta);
    }

    private static double distanceBetween(Region region1, Region region2) {
        if (region1.equals(Region.UNSPECIFIED) || region2.equals(Region.UNSPECIFIED)) {
            // For at least one region, we don't know where it is
            return Double.MAX_VALUE;
        }
        double latitudeDelta = Math.abs(region1.getLatitude() - region2.getLatitude());
        double longitudeDelta = Math.abs(region1.getLongitude() - region2.getLongitude());
        // Using trigonometry, calculate distance = sqrt(a^2 + b^2)
        return Math.sqrt(latitudeDelta * latitudeDelta + longitudeDelta * longitudeDelta);

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
            LocalDateTime requestDate1 = t1.getRequestDate().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime requestDate2 = t2.getRequestDate().truncatedTo(ChronoUnit.DAYS);
            int timeComparison = requestDate1.compareTo(requestDate2);

            if (timeComparison != 0) { // different time, so just compare using that
                return timeComparison;
            } else { // same(ish) time, so compare using location
                Client c1 = t1.getClient();
                Client c2 = t2.getClient();

                Country deathCountry = donatedOrgan.getDonor().getCountryOfDeath();

                // If they are in different countries, check which one is closest
                if (!c1.getCountry().equals(c2.getCountry())) {
                    // Check if the one of the recipients is in the same country that the donor died
                    if (c1.getCountry().equals(deathCountry)) {
                        return -1;
                    } else if (c2.getCountry().equals(deathCountry)) {
                        return 1;
                    } else { // Neither is in the same country, so calculate closest country
                        double distanceToCountry1 = distanceBetween(c1.getCountry(), deathCountry);
                        double distanceToCountry2 = distanceBetween(c1.getCountry(), deathCountry);
                        return Double.compare(distanceToCountry1, distanceToCountry2);
                    }
                }

                // If they are in the same country, but the donated organ is in a different country
                if (!c1.getCountry().equals(deathCountry)) {
                    return 0;
                }

                String deathRegion = donatedOrgan.getDonor().getRegionOfDeath();

                // If they are in different regions, check which one is closest
                // Note that for non-NZ regions, it just checks if one is the same as where the person died
                if (!c1.getRegion().equals(c2.getRegion())) {
                    // Check if the one of the recipients is in the same region that the donor died
                    if (c1.getRegion().equals(deathRegion)) {
                        return -1;
                    } else if (c2.getRegion().equals(deathRegion)) {
                        return 1;
                    } else { // Neither is in the same region, so calculate closest region
                        if (deathCountry.equals(Country.NZ)) {
                            double distanceToRegion1 = distanceBetween(
                                    Region.fromString(c1.getRegion()), Region.fromString(deathRegion));
                            double distanceToRegion2 = distanceBetween(
                                    Region.fromString(c2.getRegion()), Region.fromString(deathRegion));
                            return Double.compare(distanceToRegion1, distanceToRegion2);
                        } else { // don't know where non-NZ regions are
                            return 0;
                        }
                    }
                }

                // They are in the same region - we don't store cities, so there is no more comparisons that are doable
                return 0;
            }
        });
        return potentialMatches;
    }
}
