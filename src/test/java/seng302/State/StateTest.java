package seng302.State;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import seng302.Administrator;
import seng302.Client;
import seng302.Clinician;
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
    public void LoginClientValidTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        State.login(client);

        assertEquals(UserType.CLIENT, State.getSession().getLoggedInUserType());
        assertEquals(client, State.getSession().getLoggedInClient());
    }

    @Test
    public void LoginClinicianValidTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED, 1, "pass");

        State.login(clinician);

        assertEquals(UserType.CLINICIAN, State.getSession().getLoggedInUserType());
        assertEquals(clinician, State.getSession().getLoggedInClinician());
    }

    @Test
    public void LoginAdministratorValidTest() {
        Administrator administrator = new Administrator("username", "password");

        State.login(administrator);

        assertEquals(UserType.ADMINISTRATOR, State.getSession().getLoggedInUserType());
        assertEquals(administrator, State.getSession().getLoggedInAdministrator());
    }
}
