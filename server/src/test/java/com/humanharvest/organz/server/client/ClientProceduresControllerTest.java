package com.humanharvest.organz.server.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ClientProceduresControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private Client testClient;
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

    }



    //------------GET---------------
    //------------POST---------------

    @Test
    public void createValidProcedure() throws Exception {    }

    @Test
    public void createInvalidProcedureIncorrectDateFormat() throws Exception{    }

    @Test
    public void createInvalidProcedureInvalidAuth() throws Exception{    }

    @Test
    public void createInvalidProcedureSummary() throws Exception{    }

    @Test
    public void createInvalidProcedureDescription() throws Exception{    }
    //------------PATCH---------------
    //------------DELETE---------------
}
