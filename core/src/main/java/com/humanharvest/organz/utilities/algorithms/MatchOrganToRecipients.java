package com.humanharvest.organz.utilities.algorithms;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
     *
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

    /**
     * Compares which region is closest (r1 or r2) to the target region.
     *
     * @param r1 the first region
     * @param r2 the second region
     * @param targetRegion the target region
     * @param inNewZealand true if the target region is in New Zealand
     * @return a positive number if r2 is closest, a negative number if r1 is closest,
     * or 0 if we don't know (aka they aren't in New Zealand)
     */
    private static int compareRegionCloseness(String r1, String r2, String targetRegion, boolean inNewZealand) {
        // Check if the one of the recipients is in the same region that the donor died
        if (r1.equals(targetRegion)) {
            return -1;
        } else if (r2.equals(targetRegion)) {
            return 1;
        } else { // Neither is in the same region, so calculate closest region
            if (inNewZealand) {
                double distanceToRegion1 = distanceBetween(
                        Region.fromString(r1), Region.fromString(targetRegion));
                double distanceToRegion2 = distanceBetween(
                        Region.fromString(r2), Region.fromString(targetRegion));
                return Double.compare(distanceToRegion1, distanceToRegion2);
            } else { // don't know where non-NZ regions are
                return 0;
            }
        }
    }

    /**
     * Compares which country is closest (c1 or c2) to the target region.
     *
     * @param c1 the first country
     * @param c2 the second region
     * @param targetCountry the target country
     * @return a positive number if c2 is closest, or a negative number if c1 is closest
     */
    private static int compareCountryCloseness(Country c1, Country c2, Country targetCountry) {
        // Check if the one of the recipients is in the same country that the donor died
        if (c1.equals(targetCountry)) {
            return -1;
        } else if (c2.equals(targetCountry)) {
            return 1;
        } else { // Neither is in the same country, so calculate closest country
            double distanceToCountry1 = distanceBetween(c1, targetCountry);
            double distanceToCountry2 = distanceBetween(c2, targetCountry);
            return Double.compare(distanceToCountry1, distanceToCountry2);
        }
    }

    private static int compareLocationCloseness(Client c1, Client c2, Client targetClient) {

        Country deathCountry = targetClient.getCountryOfDeath();

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
            return compareCountryCloseness(c1.getCountry(), c2.getCountry(), deathCountry);
        }

        // If they are in the same country, but the donated organ is in a different country
        if (!c1.getCountry().equals(deathCountry)) {
            return 0;
        }

        String deathRegion = targetClient.getRegionOfDeath();

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
            boolean inNewZealand = deathCountry == Country.NZ;
            return compareRegionCloseness(c1.getRegion(), c2.getRegion(), deathRegion, inNewZealand);
        }

        // They are in the same region - we don't store cities, so there are no more comparisons that are doable
        return 0;
    }

    public static List<Client> getListOfPotentialRecipients(DonatedOrgan donatedOrgan,
            Iterable<TransplantRequest> transplantRequests) {

        List<Client> potentialMatches = new ArrayList<>();

        // If the organ trying to be matched has expired, then return an empty list
        if (donatedOrgan.hasExpired()) {
            return potentialMatches;
        }

        // Create a list of eligible transplant requests
        List<TransplantRequest> potentialTransplantRequests = new ArrayList<>();
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
            LocalDateTime requestDateTime1 = t1.getRequestDateTime().truncatedTo(ChronoUnit.DAYS);
            LocalDateTime requestDateTime2 = t2.getRequestDateTime().truncatedTo(ChronoUnit.DAYS);
            int timeComparison = requestDateTime1.compareTo(requestDateTime2);

            if (timeComparison != 0) { // different time, so just compare using that
                return timeComparison;
            } else { // same(ish) time, so compare using location
                Client c1 = t1.getClient();
                Client c2 = t2.getClient();
                Client donor = donatedOrgan.getDonor();
                return compareLocationCloseness(c1, c2, donor);
            }
        });

        // Create the list of matches from the list of transplant requests
        for (TransplantRequest transplantRequest : potentialTransplantRequests) {
            potentialMatches.add(transplantRequest.getClient());
        }

        return potentialMatches;
    }
}
