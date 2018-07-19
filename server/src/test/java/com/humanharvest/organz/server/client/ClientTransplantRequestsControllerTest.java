package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.State;
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
public class ClientTransplantRequestsControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private Client testClient;
    private String validTransplantRequestJson;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);

        validTransplantRequestJson = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
    }

    //------------POST---------------

    @Test
    public void createValidTransplantRequestTest() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[0].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[0].resolvedDate", is("2017-07-19T14:11:20.202")))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].resolvedReason", is("reason")));
    }

    @Test
    public void createValidTransplantRequestNullResolvedReasonTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\""
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[0].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[0].resolvedDate", is("2017-07-19T14:11:20.202")))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].resolvedReason", isEmptyOrNullString()));
    }

    @Test
    public void createValidTransplantRequestNullResolvedDateTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[0].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[0].resolvedDate", isEmptyOrNullString()))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].resolvedReason", is("reason")));
    }

    @Test
    public void createTransplantRequestInvalidOrganTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"invalid organ\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTransplantRequestInvalidDateTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"invalid date\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTransplantRequestInvalidStatusTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"invalid status\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTransplantRequestInvalidClientId() throws Exception {
        int invalidId = testClient.getUid() + 1;
        mockMvc.perform(post("/clients/" + invalidId + "/transplantRequests")
                .header("If-Match", testClient.getEtag())
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createTransplantRequestInvalidNoEtag() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().is(428));
    }

    @Test
    public void createTransplantRequestInvalidEtag() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getEtag() + "X")
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().is(412));
    }
}
