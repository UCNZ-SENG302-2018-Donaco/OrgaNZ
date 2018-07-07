package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
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
public class ClientControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset(false);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userIsFound() throws Exception {
        State.getClientManager().addClient(new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.firstName", is("Jan")));
    }

    @Test
    public void getTwoClientsTest() throws Exception {
        State.getClientManager().addClient(new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1));
        State.getClientManager().addClient(new Client("Fred", "Bob", "Smith", LocalDate.now(), 3));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jan")));
    }

}
