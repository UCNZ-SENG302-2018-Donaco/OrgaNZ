package com.humanharvest.organz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.Organ;
import org.junit.Ignore;
import org.junit.Test;

public class DonatedOrganTest {

    private Client donor;
    private Organ organ;
    private LocalDateTime dateTimeOfDonation;
    private DonatedOrgan donatedOrgan;

    public DonatedOrganTest(){
        donor = new Client(1);
        organ = Organ.LUNG; //4-6 hours
        dateTimeOfDonation = LocalDateTime.now();
    }

    // getDurationUntilExpiry tests

    @Test
    public void getDurationUntilExpiryTest() {
        dateTimeOfDonation = LocalDateTime.now();
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ofHours(6).getSeconds(), donatedOrgan.getDurationUntilExpiry().getSeconds(), 1);
    }

    @Test
    public void getDurationUntilExpiryHasntExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(2);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ofHours(4).getSeconds(), donatedOrgan.getDurationUntilExpiry().getSeconds(), 1);
    }

    @Test
    public void getDurationUntilExpiryJustExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(6);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ZERO, donatedOrgan.getDurationUntilExpiry());
    }

    @Test
    public void getDurationUntilExpiryExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(8);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(Duration.ZERO, donatedOrgan.getDurationUntilExpiry());
    }

    // getProgressDecimal tests

    @Test
    public void getProgressDecimalTest() {
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(0, donatedOrgan.getProgressDecimal(), 1e-3);
    }

    @Test
    public void getProgressDecimalNotInLowerBoundTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(3);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(0.5, donatedOrgan.getProgressDecimal(), 1e-3);
    }

    @Test
    public void getProgressDecimalInLowerBoundTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(5);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 5 / 6, donatedOrgan.getProgressDecimal(), 1e-3);
    }

    @Test
    public void getProgressDecimalJustExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(6);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getProgressDecimal(), 1e-3);
    }

    @Test
    public void getProgressDecimalExpiredTest() {
        dateTimeOfDonation = LocalDateTime.now().minusHours(7);
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getProgressDecimal(), 1e-3);

    }

    // getFullMarker tests

    @Test
    public void getFullMarkerLungTest() {
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 2 / 3, donatedOrgan.getFullMarker());
    }

    @Test
    public void getFullMarkerPancreasTest() {
        organ = Organ.PANCREAS;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 1 / 2, donatedOrgan.getFullMarker());
    }

    @Test
    public void getFullMarkerLiverTest() {
        organ = Organ.LIVER;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals(1, donatedOrgan.getFullMarker());
    }

    @Test
    public void getFullMarkerKidneyTest() {
        organ = Organ.KIDNEY;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 2 / 3, donatedOrgan.getFullMarker());
    }

    @Test
    public void getFullMarkerCorneaTest() {
        organ = Organ.CORNEA;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 5 / 7, donatedOrgan.getFullMarker());
    }

    @Test
    public void getFullMarkerSkinTest() {
        organ = Organ.SKIN;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        assertEquals((double) 3 / 10, donatedOrgan.getFullMarker());
    }

    // todo This should not result in an NPE. Expiration times for organs such as bone marrow are not yet implemented
    @Test
    @Ignore
    public void getFullMarkerBoneMarrowTest() {
        organ = Organ.BONE_MARROW;
        donatedOrgan = new DonatedOrgan(organ, donor, dateTimeOfDonation);
        donatedOrgan.getFullMarker();
    }
}