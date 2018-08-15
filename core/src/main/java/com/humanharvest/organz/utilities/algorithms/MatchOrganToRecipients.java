package com.humanharvest.organz.utilities.algorithms;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Region;

public class MatchOrganToRecipients {

    private static boolean agesMatch(int donorAge, int recipientAge) {
        // If one is under 12, they must both be under 12
        if (donorAge < 12 || recipientAge < 12) {
            return donorAge < 12 && recipientAge < 12;
        }

        // Otherwise (aged 12+), they must have a max age diff of 15
        return Math.abs(donorAge - recipientAge) <= 15;
    }

    private static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    /**
     * Calculates distance between two points on earth.
     * Note that there is no unit - standardising it to a unit is unnecessary, as this is just used for comparisons.
     * (To get km, multiply by 6371; to get miles, multiply by 3959)
     * Adapted from https://stackoverflow.com/a/365853/8355496
     * @param lat1 Point 1's latitude
     * @param lon1 Point 1's longitude
     * @param lat2 Point 2's latitude
     * @param lon2 Point 2's longitude
     * @return the distance between the points
     */
    private static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private static double distanceBetween(Country country1, Country country2) {
        return distanceBetween(country1.getLatitude(), country1.getLongitude(),
                country2.getLatitude(), country2.getLongitude());
    }

    private static double distanceBetween(Region region1, Region region2) {
        if (region1.equals(Region.UNSPECIFIED) || region2.equals(Region.UNSPECIFIED)) {
            // For at least one region, we don't know where it is
            return Double.MAX_VALUE;
        }
        return distanceBetween(region1.getLatitude(), region1.getLongitude(),
                region2.getLatitude(), region2.getLongitude());

    }

    public static List<Client> getListOfPotentialRecipients(DonatedOrgan donatedOrgan, Collection<TransplantRequest>
            transplantRequests) {
        List<TransplantRequest> potentialTransplantRequests = new ArrayList<>();
        List<Client> potentialMatches = new ArrayList<>();

        // If the organ trying to be matched has expired, then return an empty list
        if (donatedOrgan.hasExpired()) {
            return potentialMatches;
        }

        // Create a list of eligible transplant requests
        for (TransplantRequest transplantRequest : transplantRequests) {
            Client donor = donatedOrgan.getDonor();
            Client recipient = transplantRequest.getClient();

            if (donatedOrgan.getOrganType().equals(transplantRequest.getRequestedOrgan())
                    && donor.getBloodType() != null && recipient.getBloodType() != null
                    && donor.getBloodType().equals(recipient.getBloodType())
                    && agesMatch(donor.getAge(), recipient.getAge())) {
                potentialTransplantRequests.add(transplantRequest);
            }
        }

        // Sort the list by when the transplant request was made, then where the potential recipient lives
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

                // Check for null countries
                if (c1.getCountry() == null) {
                    if (c2.getCountry() == null) {
                        return 0; // neither has a country
                    } else {
                        return 1; // only c2 has a country
                    }
                } else if (c2.getCountry() == null) {
                    return -1; // only c1 has a country
                }

                // If they are in different countries, check which one is closest
                if (!c1.getCountry().equals(c2.getCountry())) {
                    // Check if the one of the recipients is in the same country that the donor died
                    if (c1.getCountry().equals(deathCountry)) {
                        return -1;
                    } else if (c2.getCountry().equals(deathCountry)) {
                        return 1;
                    } else { // Neither is in the same country, so calculate closest country
                        double distanceToCountry1 = distanceBetween(c1.getCountry(), deathCountry);
                        double distanceToCountry2 = distanceBetween(c2.getCountry(), deathCountry);
                        return Double.compare(distanceToCountry1, distanceToCountry2);
                    }
                }

                // If they are in the same country, but the donated organ is in a different country
                if (!c1.getCountry().equals(deathCountry)) {
                    return 0;
                }

                String deathRegion = donatedOrgan.getDonor().getRegionOfDeath();

                // Check for null regions
                if (c1.getRegion() == null) {
                    if (c2.getRegion() == null) {
                        return 0; // neither has a region
                    } else {
                        return 1; // only c2 has a region
                    }
                } else if (c2.getRegion() == null) {
                    return -1; // only c1 has a region
                }

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

                // They are in the same region - we don't store cities, so there are no more comparisons that are doable
                return 0;
            }
        });

        // Create the list of matches from the list of transplant requests
        for (TransplantRequest transplantRequest : potentialTransplantRequests) {
            potentialMatches.add(transplantRequest.getClient());
        }

        return potentialMatches;
    }
}
