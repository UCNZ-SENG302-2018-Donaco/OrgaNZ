package com.humanharvest.organz.utilities.algorithms;

public final class DistanceCalculation {

    private static final double EARTH_RADIUS_KM = 6371;

    private DistanceCalculation() {
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
    public static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * Calculates the distance between the two points given
     *
     * @param lat1 Point 1's latitude
     * @param lon1 Point 1's longitude
     * @param lat2 Point 2's latitude
     * @param lon2 Point 2's longitude
     * @return the distance between the points in km
     */
    public static double distanceBetweenInKm(double lat1, double lon1, double lat2, double lon2) {
        double distance = distanceBetween(lat1, lon1, lat2, lon2);
        return distance * EARTH_RADIUS_KM;
    }
}

