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
import com.humanharvest.organz.actions.client.MarkClientAsDeadAction;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
        Client outsider = new Client("Out", "Si", "Der", LocalDate.now(), 9997);
        Client janMichael = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 9998);
        Client michaelShoeMaker = new Client("Michael", "Shoe", "Maker", LocalDate.now(), 9999);
        janMichael.setRegionOfDeath("Auckland");
        michaelShoeMaker.setRegionOfDeath("Canterbury");
        outsider.setRegionOfDeath("Rio");
        try {
            janMichael.setOrganDonationStatus(Organ.LIVER, true);
            janMichael.setOrganDonationStatus(Organ.HEART, true);
            janMichael.setOrganDonationStatus(Organ.CORNEA, true);
            michaelShoeMaker.setOrganDonationStatus(Organ.LIVER, true);
            outsider.setOrganDonationStatus(Organ.BONE, true);
        } catch (OrganAlreadyRegisteredException ex) {
            System.out.println("Error setting up organ donation status: " + ex.getMessage());
        }

        State.getClientManager().addClient(janMichael);
        State.getClientManager().addClient(michaelShoeMaker);
        State.getClientManager().addClient(outsider);

        MarkClientAsDeadAction action1  = new MarkClientAsDeadAction(janMichael, LocalDate.now(), LocalTime.now(),
                "Auckland","Auckland", Country.NZ, State.getClientManager());
        MarkClientAsDeadAction action2  = new MarkClientAsDeadAction(michaelShoeMaker, LocalDate.now(), LocalTime.now(),
                "Canterbury","Canterbury", Country.NZ, State.getClientManager());
        MarkClientAsDeadAction action3  = new MarkClientAsDeadAction(outsider, LocalDate.now(), LocalTime.now(),
                "Rio","Rio", Country.BR, State.getClientManager());

        State.getActionInvoker("").execute(action1);
        State.getActionInvoker("").execute(action2);
        State.getActionInvoker("").execute(action3);


    }

    @Test
    public void getDefault() throws Exception {
        mockMvc.perform(get("/clients/organs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(5)));
    }


    @Test
    public void getFilteredOrgans() throws Exception {
        mockMvc.perform(get("/clients/organs?organType=LIVER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(2)));
    }

    @Test
    public void getMultipleFilteredOrgans() throws Exception {
        mockMvc.perform(get("/clients/organs?organType=LIVER,HEART,CORNEA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(4)));
    }

    @Test
    public void getFilteredRegion() throws Exception {
        mockMvc.perform(get("/clients/organs?regions=Auckland"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(3)));
    }

    @Test
    public void getMultipleFilteredRegions() throws Exception {
        mockMvc.perform(get("/clients/organs?regions=Auckland,Canterbury"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(4)));
    }

    @Test
    public void getRegionOutsideNZ() throws Exception {
        mockMvc.perform(get("/clients/organs?regions=International"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(1)));
    }

    @Test
    public void filterRegionAndOrgans() throws Exception {
        mockMvc.perform(get("/clients/organs?regions=Auckland&organType=LIVER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(1)));
    }
}
