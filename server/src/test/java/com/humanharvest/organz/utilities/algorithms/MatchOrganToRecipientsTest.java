package com.humanharvest.organz.utilities.algorithms;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.state.State.DataStorageType;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.Before;
import org.junit.Test;

public class MatchOrganToRecipientsTest {

    private Client recipient1 = new Client(1);
    private Client recipient2 = new Client(2);
    private Client donor = new Client(3);
    private LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
    private Collection<DonatedOrgan> organsToDonate;

    @Before
    public void setUp() {
        State.init(DataStorageType.MEMORY);
        State.reset();

        // Add clients to list
        List<Client> clients = new ArrayList<>();
        clients.add(recipient1);
        clients.add(recipient2);
        clients.add(donor);

        // Make transplant requests for recipients
        TransplantRequest transplantRequest1 = new TransplantRequest(recipient1, Organ.LIVER);
        recipient1.addTransplantRequest(transplantRequest1);
        TransplantRequest transplantRequest2 = new TransplantRequest(recipient2, Organ.LIVER);
        recipient2.addTransplantRequest(transplantRequest2);

        // Set country and region for recipients
        recipient1.setCountry(Country.NZ);
        recipient1.setRegion(Region.CANTERBURY.toString());
        recipient2.setCountry(Country.NZ);
        recipient2.setRegion(Region.CANTERBURY.toString());

        // Set blood type
        recipient1.setBloodType(BloodType.A_POS);
        recipient2.setBloodType(BloodType.A_POS);
        donor.setBloodType(BloodType.A_POS);

        // Set dates of birth
        recipient1.setDateOfBirth(dateOfBirth);
        recipient2.setDateOfBirth(dateOfBirth);
        donor.setDateOfBirth(dateOfBirth);

        // Setup donor's organ to donate, and death
        donor.donateOrgan(Organ.LIVER, LocalDateTime.now());
        donor.setDateOfDeath(LocalDate.now());
        donor.setTimeOfDeath(LocalTime.now());
        donor.setCountryOfDeath(Country.NZ);
        donor.setRegionOfDeath(Region.CANTERBURY.toString());
        donor.setCityOfDeath("Christchurch");

        State.getClientManager().setClients(clients);
        assertEquals(2, State.getClientManager().getAllCurrentTransplantRequests().size());

        organsToDonate = State.getClientManager().getAllOrgansToDonate();
        assertEquals(1, organsToDonate.size());
    }

    private void assertIsFirstInLine(Client recipient) {
        for (DonatedOrgan donatedOrgan : organsToDonate) {
            List<Client> eligibleClients = MatchOrganToRecipients.getListOfPotentialRecipients(donatedOrgan);
            assertEquals(recipient, eligibleClients.get(0));
        }
    }

    @Test
    public void testBothDifferentCountries() {
        recipient1.setCountry(Country.AU);
        recipient2.setCountry(Country.GB);

        // recipient 1 is much closer to the donor
        assertIsFirstInLine(recipient1);
    }

    @Test
    public void testBothDifferentCountries2() {
        recipient1.setCountry(Country.GB);
        recipient2.setCountry(Country.AU);

        // recipient 2 is much closer to the donor
        assertIsFirstInLine(recipient2);
    }

    @Test
    public void testOneInSameCountry() {
        recipient1.setCountry(Country.GB);
        recipient2.setCountry(Country.NZ);

        // recipient 2 is in the same country
        assertIsFirstInLine(recipient2);
    }
}
