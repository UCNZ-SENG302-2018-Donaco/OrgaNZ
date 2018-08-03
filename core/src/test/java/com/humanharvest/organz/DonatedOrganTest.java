package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.Organ;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DonatedOrganTest {

    private static final String FIRST_NAME = "First";
    private static final String MIDDLE_NAME = "middlename";
    private static final String PREFERRED_NAME = "Preferred";

    private Client donor;
    private Organ organ;
    private LocalDateTime dateTimeOfDonation;
    private DonatedOrgan donatedOrgan;

    @BeforeEach
    void setup() {
        donor = new Client(1);
        organ = Organ.LUNG; //4-6 hours
        dateTimeOfDonation = LocalDateTime.now();
    }

    // getDurationUntilExpiry tests

    @Test
    void getDurationUntilExpiryTest() {
        dateTimeOfDonation = LocalDateTime.now();
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ofHours(6), donatedOrgan.getDurationUntilExpiry());
    }

    @Test
    void getDurationUntilExpiryHasntExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(2);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ofHours(4), donatedOrgan.getDurationUntilExpiry());
    }

    @Test
    void getDurationUntilExpiryJustExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(6);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ZERO, donatedOrgan.getDurationUntilExpiry());
    }

    @Test
    void getDurationUntilExpiryExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(8);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ZERO, donatedOrgan.getDurationUntilExpiry());
    }

    // getProgressDecimal tests

    @Test
    void getProgressDecimalTest() {
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(0, donatedOrgan.getProgressDecimal(), 1e-6);
    }

    @Test
    void getProgressDecimalNotInLowerBoundTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(3);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(0.5, donatedOrgan.getProgressDecimal(), 1e-6);
    }

    @Test
    void getProgressDecimalInLowerBoundTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(5);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 5 / 6, donatedOrgan.getProgressDecimal(), 1e-6);
    }

    @Test
    void getProgressDecimalJustExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(6);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getProgressDecimal(), 1e-6);
    }

    @Test
    void getProgressDecimalExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(7);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getProgressDecimal(), 1e-6);
    }

    // getFullMarker tests

    @Test
    void getFullMarkerLungTest() {
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 2 / 3, donatedOrgan.getFullMarker());
    }

    @Test
    void getFullMarkerPancreasTest() {
        organ = Organ.PANCREAS;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 1 / 2, donatedOrgan.getFullMarker());
    }

    @Test
    void getFullMarkerLiverTest() {
        organ = Organ.LIVER;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getFullMarker());
    }

    @Test
    void getFullMarkerKidneyTest() {
        organ = Organ.KIDNEY;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 2 / 3, donatedOrgan.getFullMarker());
    }

    @Test
    void getFullMarkerCorneaTest() {
        organ = Organ.CORNEA;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 5 / 7, donatedOrgan.getFullMarker());
    }

    @Test
    void getFullMarkerSkinTest() {
        organ = Organ.SKIN;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 3 / 10, donatedOrgan.getFullMarker());
    }

    // todo This should not result in an NPE. Expiration times for organs such as bone marrow are not yet implemented
    @Test
    @Ignore
    void getFullMarkerBoneMarrowTest() {
        organ = Organ.BONE_MARROW;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        donatedOrgan.getFullMarker();
    }
}