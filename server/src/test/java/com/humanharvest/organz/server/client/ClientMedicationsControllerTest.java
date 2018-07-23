package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;
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
public class ClientMedicationsControllerTest {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private Client testClient;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Test", "User", "Name", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);

        MedicationRecord testRecord = new MedicationRecord("Name", LocalDate.now(), null);
        testClient.addMedicationRecord(testRecord);
        MedicationRecord testMedication2 = new MedicationRecord("Name2", LocalDate.now(), null);
        testClient.addMedicationRecord(testMedication2);

    }

    // ------------------GET MedicationRecord----------------------------

    @Test
    public void clientNotFound() throws Exception {
        mockMvc.perform(get("/clients/2/medications"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTwoMedications() throws Exception {
        mockMvc.perform(get("/clients/1/medications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].medicationName", is("Name")));
    }

    //----------------------POST Create MedicationRecord------------------

    /**
    public void createMedication() throws Exception {
        CreateMedicationRecordView recordView = new CreateMedicationRecordView();
        recordView.setName("MedicationName");
        String json = "{ \"name\": \"testmed\" }";

        mockMvc.perform(post("/clients/1/medications")
                .contentType(contentType)
                .content(json)
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[2].medicationName", is("testmed")))
                .andExpect(jsonPath("$[2].stopped", is(((String) null))));
    } **/

    @Test
    public void createMedicationMissingEtag() throws Exception {
        String json = "{ \"name\": \"testmed1\" }";
        mockMvc.perform(post("/clients/1/medications")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isPreconditionRequired());
    }

    @Test
    public void createMedicationInvalidEtag() throws Exception {
        String json = "{ \"name\": \"testmed1\" }";
        mockMvc.perform(post("/clients/1/medications")
                .contentType(contentType)
                .content(json)
                .header("If-Match", "invalidEtag"))
                .andExpect(status().isPreconditionFailed());
    }

    /**

    public void testCreateMedicationInvalid() throws Exception {
        CreateMedicationRecordView recordView = new CreateMedicationRecordView();
        recordView.setName("MedicationName");
        String json = "{ \"invalid\": \"testmed\" }";

        mockMvc.perform(post("/clients/1/medications")
                .contentType(contentType)
                .content(json)
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isBadRequest());
    } **/

    //------------------------DELETE-----------------------

    @Test
    public void testDeleteMedication() throws Exception {
        mockMvc.perform(delete("/clients/1/medications/1")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clients/1/medications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].medicationName", is("Name")));
    }

    @Test
    public void deleteMedicationNotFound() throws Exception {
        mockMvc.perform(delete("/clients/1/medications/3")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isNotFound());
    }

    //---------------POST start/stop medication

    @Test
    public void startMedication() throws Exception {
        // Set to a past medication first
        mockMvc.perform(post("/clients/1/medications/0/stop")
                .header("If-Match", testClient.getETag()));

        mockMvc.perform(post("/clients/1/medications/0/start")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.stopped", is((String) null)));
    }

    @Test
    public void startMedicationNotFound() throws Exception {
        mockMvc.perform(post("/clients/1/medications/3/start")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void stopMedication() throws Exception {
        mockMvc.perform(post("/clients/1/medications/0/stop")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.stopped", is(LocalDate.now().toString())));
    }
}
