package com.humanharvest.organz;

import com.humanharvest.organz.state.Session.UserType;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class StateTest extends BaseTest {

    @Before
    public void init() {
        State.reset();
    }

    @Test
    public void LoginClientValidTest() {
        Client client = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);
        State.getClientManager().addClient(client);
        State.login(client);

        assertEquals(UserType.CLIENT, State.getSession().getLoggedInUserType());
        assertEquals(client, State.getSession().getLoggedInClient());
    }

    @Test
    public void LoginClinicianValidTest() {
        Clinician clinician = new Clinician("First", null, "Last", "Address", Region.UNSPECIFIED.toString(), null, 1, "pass");

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
