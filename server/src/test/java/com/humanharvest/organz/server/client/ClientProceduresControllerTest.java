package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
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
public class ClientProceduresControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private Client testClient;
    private String validProcedureJson;
    private String VALID_AUTH = "valid auth";
    private String INVALID_AUTH = "invalid auth";

    LocalDate localDate;
    ProcedureRecord procedureRecord1 = new ProcedureRecord(
            "Liver transplant",
            "Patient needs a liver transplant,",
            localDate);
    ProcedureRecord procedureRecord2 = new ProcedureRecord(
            "Hip-bone reconstruction",
            "Patient needs a hip-bone reconstruction",
            localDate);

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init(){
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
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

        validProcedureJson = "{ \n" +
                "\"summary\": \"Heart Transplant\", \n" +
                "\"description\": \"To fix my achy-breaky heart.\", \n" +
                "\"date\": \"2017-06-01\", \n" +
                "\"affectedOrgans\": [\"HEART\"] \n"+
                " }";
    }

    //------------GET----------------

    @Test
    public void getValidClient() throws Exception {
    }

    @Test
    public void getInvalidClient() throws Exception {    }

    //------------POST---------------


    @Test
    public void createValidProcedure() throws Exception {
        mockMvc.perform(post("/clients/1/procedures")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(validProcedureJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[2].summary", is("Heart Transplant")))
                .andExpect(jsonPath("$[2].description", is("To fix my achy-breaky heart.")))
                .andExpect(jsonPath("$[2].date", is("2017-06-01")))
                .andExpect(jsonPath("$[2].affectedOrgans[0]", is("HEART")));
    }

    @Test
    public void createInvalidProcedureIncorrectDateFormat() throws Exception{    }

    @Test
    public void createInvalidProcedureInvalidAuth() throws Exception{    }

    @Test
    public void createInvalidProcedureSummary() throws Exception{    }

    @Test
    public void createInvalidProcedureDescription() throws Exception{    }

    //------------PATCH---------------


    @Test
    public void validPatch() throws Exception {/*
        String json = "{ \"description\": \"new\" }";

        mockMvc.perform(patch("/clients/1/procedures/2")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isOk());*/
    }

    @Test
    public void invalidAuthPatch() throws Exception {   }

    @Test
    public void invalidDatePatch() throws Exception {   }

    @Test
    public void invalidETagPatch() throws Exception {   }

    //------------DELETE---------------

    @Test
    public void validDelete() throws Exception {   }

    @Test
    public void invalidDeleteWrongProcedure() throws Exception {   }

    @Test
    public void invalidETagDelete() throws Exception {   }
}
