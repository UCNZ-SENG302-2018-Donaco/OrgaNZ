package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class OrganControllerTest {

    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        Client janMichael = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 9998);
        Client michaelShoeMaker = new Client("Michael", "Shoe", "Maker", LocalDate.now(), 9999);
        janMichael.setRegion("Auckland");
        michaelShoeMaker.setRegion("Canterbury");
        try {
            janMichael.setOrganDonationStatus(Organ.LIVER, true);
            michaelShoeMaker.setOrganDonationStatus(Organ.LIVER, true);
        } catch (OrganAlreadyRegisteredException ex) {
            System.out.println("Error setting up organ donation status: " + ex.getMessage());
        }
        janMichael.setDateOfDeath(LocalDate.now()); //TODO find how to mark a client as dead and have this register.
        janMichael.setTimeOfDeath(LocalTime.now());
        janMichael.setCountryOfDeath(Country.US);
        janMichael.setRegionOfDeath("New York");
        janMichael.setCityOfDeath("New York City");

        State.getClientManager().addClient(janMichael);
        State.getClientManager().addClient(michaelShoeMaker);

    }

    @Test
    public void getDefault() throws Exception {
        mockMvc.perform(get("/clients/organs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

    }

}
