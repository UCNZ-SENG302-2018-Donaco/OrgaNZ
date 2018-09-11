package com.humanharvest.organz.utilities.algorithms;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

public class DistanceCalculationTest {

    // Auckland Hospital
    private double lat1 = -36.8604597;
    private double lon1 = 174.7691264;

    // Christchurch Hospital
    private double lat2 = -43.5336199;
    private double lon2 = 172.626228;



    @Test
    public void distanceBetweenTest() {

        // Wellington Hospital
        double lat3 = -41.3085774;
        double lon3 = 174.7790445;

        double distance1 = DistanceCalculation.distanceBetween(lat1, lon1, lat2, lon2);
        double distance2 = DistanceCalculation.distanceBetween(lat3, lon3, lat2, lon2);
        // Ensure that the value for Auckland -> Christchurch is greater than Wellington -> Christchurch
        Assert.assertTrue(distance1 > distance2);
    }

    @Test
    public void distanceBetweenInKmTest() {
        double distanceInKm = DistanceCalculation.distanceBetweenInKm(lat1, lon1, lat2, lon2);
        assertEquals(763.9, distanceInKm, 0.1);
    }

    @Test
    public void invalidLatitudeTest() {
        double invalidLat = Double.NaN;
        double distance = DistanceCalculation.distanceBetween(invalidLat, lon1, lat2, lon2);
        assertEquals(Double.NaN, distance);
    }
}
