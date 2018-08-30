package com.humanharvest.organz.server.controller.client;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

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
public class ClientIllnessesControllerTest {

    IllnessRecord record1 = new IllnessRecord("Tuberculosis", LocalDate.of(2018, 4, 2),
            null, false);
    IllnessRecord record2 = new IllnessRecord("Influenza", LocalDate.of(2015, 2, 2),
            LocalDate.of(2015, 6, 6), false);
    @Autowired
    WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Client testClient;
    private String validProcedureJson;
    private String VALID_AUTH = "valid auth";
    private String INVALID_AUTH = "invalid auth";

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Tom", "Middle", "Last", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);
        testClient.addIllnessRecord(record1);
        //testClient.addIllnessRecord(record2);
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        State.setAuthenticationManager(mockAuthenticationManager);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(INVALID_AUTH);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(null);
        doNothing().when(mockAuthenticationManager).verifyClinicianOrAdmin(VALID_AUTH);

        validProcedureJson = " {\n"
                + "        \"illnessName\": \"Uncured Tuberculosis\",\n"
                + "        \"diagnosisDate\": \"2018-01-01\",\n"
                + "        \"curedDate\": null,\n"
                + "        \"chronic\": false\n"
                + "    }";
        State.getClientManager().applyChangesTo(testClient);

    }

    @Test
    public void getClientsIllnesses() throws Exception {
        mockMvc.perform(get("/clients/1/illnesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        //.andExpect(jsonPath("$.illnessName", is("Tuberculosis")));

    }

    @Test
    public void patchClientIllness() throws Exception {
        String patch = "{\n"
                + "\t\"illnessName\":\"Patchy\",\n"
                + "\t\"diagnosisDate\":\"2018-01-01\"\n"
                + "}";
        mockMvc.perform(patch("/clinicians/0")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(patch))
                .andExpect(status().isOk());
    }

    @Test
    public void addValidIllness() throws Exception {

        mockMvc.perform(post("/clients/1/illnesses")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validProcedureJson))
                .andExpect(status().isCreated());

    }

    @Test
    public void deleteIllness() throws Exception {
        testClient.addIllnessRecord(record2);
        mockMvc.perform(delete("/clients/1/illnesses/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNonExistingIllness() throws Exception {
        mockMvc.perform(delete("/clients/illnesses/10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void nonExistingClient() throws Exception {
        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound());
    }

}
