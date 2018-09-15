package com.humanharvest.organz.server.controller.client;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
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
public class ClientProceduresControllerTest {

    private static final String VALID_AUTH = "valid auth";
    private static final String INVALID_AUTH = "invalid auth";
    @Autowired
    WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private Client testClient;
    private LocalDate localDate;
    private final ProcedureRecord procedureRecord1 = new ProcedureRecord(
            "Liver transplant",
            "Patient needs a liver transplant,",
            localDate);
    private final ProcedureRecord procedureRecord2 = new ProcedureRecord(
            "Hip-bone reconstruction",
            "Patient needs a hip-bone reconstruction",
            localDate);

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("John", "Freddy", "Doe", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);
        testClient.addProcedureRecord(procedureRecord1);
        testClient.addProcedureRecord(procedureRecord2);

        // Create mock authentication manager that verifies all clinicianOrAdmins
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        State.setAuthenticationManager(mockAuthenticationManager);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(INVALID_AUTH);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(null);
        doNothing().when(mockAuthenticationManager).verifyClinicianOrAdmin(VALID_AUTH);
    }

    //------------GET----------------

    @Test
    public void getValidClient() throws Exception {
        mockMvc.perform(get("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void getInvalidClient() throws Exception {
        mockMvc.perform(get("/clients/5/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    //------------POST---------------

    @Test
    public void createValidProcedure() throws Exception {
        String validProcedureJson = "{ \n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"To fix my achy-breaky heart.\", \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validProcedureJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$[2].summary", is("Heart Transplant")))
                .andExpect(jsonPath("$[2].description", is("To fix my achy-breaky heart.")))
                .andExpect(jsonPath("$[2].date", is("2017-06-01")))
                .andExpect(jsonPath("$[2].affectedOrgans[0]", is("HEART")));
    }

    @Test
    public void createInvalidProcedureIncorrectDateFormat() throws Exception {
        String invalidDateProcedureJson = "{ \n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"To fix my achy-breaky heart.\", \n" +
                "\"date\": \"2017-060-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(invalidDateProcedureJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidProcedureInvalidAuth() throws Exception {
        String validProcedureJson = "{ \n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"To fix my achy-breaky heart.\", \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", INVALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validProcedureJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createInvalidProcedureSummary() throws Exception {
        String validProcedureJson = "{ \n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"summary\": e, \n" +
                "\"description\": \"To fix my achy-breaky heart.\", \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validProcedureJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidProcedureDescription() throws Exception {
        String validProcedureJson = "{ \n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"summary\": \"summary\", \n" +
                "\"description\": e, \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(validProcedureJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void resolveTransplantInvalidAuth() throws Exception {
        mockMvc.perform(post(
                "/clients/" + testClient.getUid() + "/transplants/12/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", INVALID_AUTH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void resolveTransplantInvalidClient() throws Exception {
        mockMvc.perform(post(
                "/clients/12131/transplants/12/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void resolveTransplantOnProcedureRecord() throws Exception {
        mockMvc.perform(post(
                "/clients/" + testClient.getUid() + "/transplants/1/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isNotFound());
    }

    @Test
    public void resolveTransplantTwice() throws Exception {
        Client donor = new Client(1231);

        TransplantRequest request = new TransplantRequest(testClient, Organ.LIVER);
        testClient.addTransplantRequest(request);

        DonatedOrgan donatedOrgan = new DonatedOrgan(Organ.LIVER, donor, LocalDateTime.now(), (long) 1232131);

        TransplantRecord transplantRecord = new TransplantRecord(donatedOrgan, request,
                Hospital.getDefaultHospitals().iterator().next(), LocalDate.now().plusDays(1));
        transplantRecord.setId(12);

        testClient.addProcedureRecord(transplantRecord);

        mockMvc.perform(post(
                "/clients/" + testClient.getUid() + "/transplants/12/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk());

        mockMvc.perform(post(
                "/clients/" + testClient.getUid() + "/transplants/12/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void successfullyResolveTransplant() throws Exception {
        Client donor = new Client(1231);

        TransplantRequest request = new TransplantRequest(testClient, Organ.LIVER);
        testClient.addTransplantRequest(request);

        DonatedOrgan donatedOrgan = new DonatedOrgan(Organ.LIVER, donor, LocalDateTime.now(), (long) 1232131);

        TransplantRecord transplantRecord = new TransplantRecord(donatedOrgan, request,
                Hospital.getDefaultHospitals().iterator().next(), LocalDate.now());
        transplantRecord.setId(12);

        testClient.addProcedureRecord(transplantRecord);

        mockMvc.perform(post(
                "/clients/" + testClient.getUid() + "/transplants/12/complete")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk());
    }

    //------------PATCH---------------

    @Test
    public void invalidAuthPatch() throws Exception {
        String json = "{\n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"id\": 2, \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"New Description\", \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";

        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/procedures/2")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", INVALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void invalidDatePatch() throws Exception {
        String json = "{\n" +
                "\"type\": \"ProcedureRecord\", \n" +
                "\"id\": 2, \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"New Description\", \n" +
                "\"date\": \"2017-06-01-9\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n" +
                " }";

        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/procedures/2")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    //------------DELETE---------------

    @Test
    public void invalidDeleteWrongProcedure() throws Exception {
        mockMvc.perform(delete("/clients/1/procedures/5")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isNotFound());
    }
}
