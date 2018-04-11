package seng302.State;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Clinician;
import seng302.Donor;
import seng302.State.Session.UserType;
import seng302.Utilities.Enums.Region;

import org.junit.Before;
import org.junit.Test;

public class StateTest {

    @Before
    public void init() {
        State.init();
        State.logout();
    }

    @Test
    public void LoginDonorValidTest() {
        Donor loginDonor = new Donor("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        State.login(loginDonor);

        assertEquals(UserType.DONOR, State.getSession().getLoggedInUserType());
        assertEquals(loginDonor, State.getSession().getLoggedInDonor());
    }

    @Test
    public void LoginClinicianValidTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");

        State.login(clinician);

        assertEquals(UserType.CLINICIAN, State.getSession().getLoggedInUserType());
        assertEquals(clinician, State.getSession().getLoggedInClinician());
    }

}
