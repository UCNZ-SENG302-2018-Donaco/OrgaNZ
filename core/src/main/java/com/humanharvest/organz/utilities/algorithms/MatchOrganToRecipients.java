package com.humanharvest.organz.utilities.algorithms;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.Hospital;
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

    private static double distanceBetween(Country country1, Country country2) {
        return DistanceCalculation.distanceBetween(country1.getLatitude(), country1.getLongitude(),
                country2.getLatitude(), country2.getLongitude());
    }

    private static double distanceBetween(Region region1, Region region2) {
        if (region1.equals(Region.UNSPECIFIED) || region2.equals(Region.UNSPECIFIED)) {
            // For at least one region, we don't know where it is
            return Double.MAX_VALUE;
        }
        return DistanceCalculation.distanceBetween(region1.getLatitude(), region1.getLongitude(),
                region2.getLatitude(), region2.getLongitude());
    }

    private static double distanceBetween(Hospital h1, Hospital h2) {
        return DistanceCalculation.distanceBetween(h1.getLatitude(), h1.getLongitude(),
                h2.getLatitude(), h2.getLongitude());
    }

    /**
     * Compares which country is closest (c1 or c2) to the target country.
     *
     * @param c1 the first country
     * @param c2 the second country
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
     * Compares which hospital is closest (h1 or h2) to the target hospital.
     *
     * @param h1 the first hospital
     * @param h2 the second hospital
     * @param targetHospital the target hospital
     * @return a positive number if h2 is closest, or a negative number if h1 is closest
     */
    private static int compareHospitalCloseness(Hospital h1, Hospital h2, Hospital targetHospital) {
        // Check if the one of the recipients is in the same hospital that the donor died
        if (h1.equals(targetHospital)) {
            return -1;
        } else if (h2.equals(targetHospital)) {
            return 1;
        } else { // Neither is in the same hospital, so calculate closest hospital
            double distanceToHospital1 = distanceBetween(h1, targetHospital);
            double distanceToHospital2 = distanceBetween(h2, targetHospital);
            return Double.compare(distanceToHospital1, distanceToHospital2);
        }
    }

    /**
     * "Compares" which object is not null. This results in them being sorted so that non-nulls come first.
     *
     * @param o1 the first object
     * @param o2 the second object
     * @return a positive number if o2 isn't null, a negative number if o1 isn't null, 0 if both are null, or null if
     * neither are null
     */
    private static Integer compareNulls(Object o1, Object o2) {
        if (o1 == null) {
            if (o2 == null) {
                return 0; // both null
            } else {
                return 1; // only o2 isn't null
            }
        } else if (o2 == null) {
            return -1; // only o1 isn't null
        }
        return null; // neither are null
    }

    /**
     * Compares two clients' closeness to the target client using the following rules:
     *
     * First, countries are compared:
     * If only one has a country, they have precedence.
     * * If neither have a country, they are equal.
     * * If they are in different countries, the one in the closest country has precedence.
     * * If they are in the same country, but the donated organ is in a different country, they are equal.
     *
     * They are now both in the same country as the target client.
     * If any client doesn't have a region, then hospitals are compared:
     * * If only one has a hospital, they have precedence.
     * * If they both have hospitals:
     * * * if they are in different hospitals, then the one in the closest hospital has precedence.
     * * * if they are in the same hospital, they are equal.
     * * If neither have hospitals, then regions are compared:
     * * * If only one has a region, they have precedence.
     * * * If neither has a region, or there is no target region, they are equal.
     *
     * They now both have regions.
     * If they are in different regions, then regions are compared:
     * * If either is in the same region as the target, they have precedence.
     * * If neither is in the same region as the target (and they are in New Zealand),
     * the one in the closest region has precedence.
     *
     * They are now in the same region.
     * If they both have hospitals, then the hospitals are compared:
     * * If they are in the same hospital, they are equal.
     * * Otherwise, the one in the closest hospital (or the same hospital as the target) has precedence.
     * If only one has a hospital, they have precedence.
     * If neither have a hospital, they are equal.
     *
     * @param c1 the first client
     * @param c2 the second client
     * @param targetClient the target client
     * @return a positive number if h2 is closest, a negative number if h1 is closest,
     * or 0 if we don't know (or they are both in the same hospital)
     */
    private static int compareLocationCloseness(Client c1, Client c2, Client targetClient) {

        Country country1 = c1.getCountry();
        Country country2 = c2.getCountry();
        Country deathCountry = targetClient.getCountryOfDeath();

        // Check for null countries
        Integer countryNullComparison = compareNulls(country1, country2);
        if (countryNullComparison != null) {
            return countryNullComparison;
        }

        // If they are in different countries, check which one is closest
        if (!country1.equals(country2)) {
            return compareCountryCloseness(country1, country2, deathCountry);
        }

        // If they are in the same country, but the donated organ is in a different country
        if (!country1.equals(deathCountry)) {
            return 0;
        }

        String r1 = c1.getRegion();
        String r2 = c2.getRegion();
        String deathRegion = targetClient.getRegionOfDeath();

        Hospital h1 = c1.getHospital();
        Hospital h2 = c2.getHospital();
        Hospital deathHospital = targetClient.getHospital();

        // Check if any regions are null, and compare hospitals
        if (r1 == null || r2 == null || deathRegion == null) {
            // Check if any of the hospitals are null
            Integer comparison = compareNulls(h1, h2);
            if (comparison == null) {
                // neither hospital is null
                return compareHospitalCloseness(h1, h2, deathHospital);
            } else if (comparison == 0) {
                // both hospitals are null - do region null comparison
                Integer regionNullComparison = compareNulls(r1, r2);
                return regionNullComparison == null ? 0 : regionNullComparison;
            } else {
                // one hospital is null
                return comparison;
            }
        }

        // If they are in different regions, check which one is closest
        // Note that for non-NZ regions, it just checks if one is the same as where the person died
        if (!r1.equals(r2)) {
            boolean inNewZealand = deathCountry == Country.NZ;
            return compareRegionCloseness(r1, r2, deathRegion, inNewZealand);
        }

        // They are in the same region - compare hospitals
        // Check if any of the hospitals are null
        Integer comparison = compareNulls(h1, h2);
        if (comparison == null) {
            // neither hospital is null
            return compareHospitalCloseness(h1, h2, deathHospital);
        } else if (comparison == 0) {
            // both hospitals are null
            return 0;
        } else {
            // one hospital is null
            return comparison;
        }
    }

    public static List<Client> getListOfPotentialRecipients(DonatedOrgan donatedOrgan,
            Iterable<TransplantRequest> transplantRequests) {

        List<Client> potentialMatches = new ArrayList<>();

        Client donor = donatedOrgan.getDonor();

        // If the organ trying to be matched has expired, or the donor isn't registered to a hospital,
        // then return an empty list.
        if (donatedOrgan.hasExpired() || donor.getHospital() == null) {
            return potentialMatches;
        }

        // Create a list of eligible transplant requests
        List<TransplantRequest> potentialTransplantRequests = new ArrayList<>();
        for (TransplantRequest transplantRequest : transplantRequests) {
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
