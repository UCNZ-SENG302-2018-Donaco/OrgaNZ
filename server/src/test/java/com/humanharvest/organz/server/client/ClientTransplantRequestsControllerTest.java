package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
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
    private TransplantRequest transplantRequest;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);
        transplantRequest = new TransplantRequest(testClient, Organ.LIVER);
    }

    //------------POST---------------

    @Test
    public void createValidTransplantRequestTest() throws Exception {
        String json = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2018-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2018-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[0].requestDate", is("2018-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[0].resolvedDate", is("2018-07-19T14:11:20.202")))
                .andExpect(jsonPath("$[0].status", is("WAITING")))
                .andExpect(jsonPath("$[0].resolvedReason", is("reason")));
    }
}
