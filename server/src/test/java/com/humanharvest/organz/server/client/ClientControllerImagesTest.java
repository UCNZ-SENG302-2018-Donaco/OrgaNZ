package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ClientControllerImagesTest {

    private MockMvc mockMvc;
    private Client testClient;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("", "", "", LocalDate.now(), 9999);
        State.getClientManager().addClient(testClient);
        State.setAuthenticationManager(new AuthenticationManagerFake());
    }

    @After
    public void tearDown() {
        File file = new File("./../images/9999.png");
        file.delete();
    }


    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void getNonExistingImage() throws Exception {
        mockMvc.perform(get("/clients/9999/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void getExistingImage() throws Exception {
        InputStream in = new FileInputStream("./../images/default.png");
        byte[] bytes = IOUtils.toByteArray(in);

        // Post an image
        mockMvc.perform(post("/clients/9999/image")
                .contentType(MediaType.IMAGE_PNG)
                .content(bytes));

        // Test that the image belongs to the right client.
        mockMvc.perform(get("/clients/9999/image"))
                .andExpect(status().isOk());
    }


    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void validPost() throws Exception {
        InputStream in = new FileInputStream("./../images/default.png");
        byte[] bytes = IOUtils.toByteArray(in);

        mockMvc.perform(post("/clients/9999/image")
                .contentType(MediaType.IMAGE_PNG)
                .content(bytes))
                .andExpect(status().isOk());
    }

    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void invalidPost() throws Exception {
        InputStream in = new FileInputStream("./../images/default.png");
        byte[] bytes = IOUtils.toByteArray(in);

        mockMvc.perform(post("/clients/9998/image")
                .contentType(MediaType.IMAGE_PNG)
                .content(bytes))
                .andExpect(status().isNotFound());

    }

    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void validDelete() throws Exception {
        InputStream in = new FileInputStream("./../images/default.png");
        byte[] bytes = IOUtils.toByteArray(in);

        mockMvc.perform(post("/clients/9999/image")
                .contentType(MediaType.IMAGE_PNG)
                .content(bytes))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/clients/9999/image"))
                .andExpect(status().isOk());
    }

    @Test
    @Ignore // TODO Figure out why pathnames need extra ./"../"images999.png !!!
    public void invalidDelete() throws Exception {
        mockMvc.perform(delete("/clients/9999/image"))
                .andExpect(status().isNotFound());
    }

}
