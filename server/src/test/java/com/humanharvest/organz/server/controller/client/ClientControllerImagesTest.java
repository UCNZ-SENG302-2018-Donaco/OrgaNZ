package com.humanharvest.organz.server.controller.client;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
public class ClientControllerImagesTest {

    private MockMvc mockMvc;
    private Client testClient;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        State.setImageDirectory(temporaryFolder.toString());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("", "", "", LocalDate.now(), 9999);
        State.getClientManager().addClient(testClient);
    }

    /**
     * We need some slightly complex tear down logic because TemporaryFolder wasn't deleting for some reason.
     * Possibly due to symlink checks?
     *
     * @throws IOException Shouldn't happen
     */
    @After
    public void destroy() throws IOException {
        Path folder = Paths.get(temporaryFolder.toString());
        if (Files.exists(folder)) {
            Files.walk(folder)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    public void getNonExistingImage() throws Exception {
        mockMvc.perform(get("/clients/9999/image")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getExistingImage() throws Exception {
        try (InputStream in = new FileInputStream("./../images/default.png")) {
            byte[] bytes = IOUtils.toByteArray(in);
            // Post an image
            mockMvc.perform(post("/clients/9999/image")
                    .header("If-Match", testClient.getETag())
                    .contentType(MediaType.IMAGE_PNG)
                    .content(bytes));

            // Test that the image belongs to the right client.
            mockMvc.perform(get("/clients/9999/image")
                    .header("If-Match", testClient.getETag()))
                    .andExpect(status().isOk());
        }
    }


    @Test
    public void validPost() throws Exception {
        try (InputStream in = new FileInputStream("./../images/default.png")) {
            byte[] bytes = IOUtils.toByteArray(in);

            mockMvc.perform(post("/clients/9999/image")
                    .contentType(MediaType.IMAGE_PNG)
                    .header("If-Match", testClient.getETag())
                    .content(bytes))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void validReplacingPost() throws Exception {
        try (InputStream in = new FileInputStream("./../images/default.png")) {
            byte[] bytes = IOUtils.toByteArray(in);

            mockMvc.perform(post("/clients/9999/image")
                    .contentType(MediaType.IMAGE_PNG)
                    .header("If-Match", testClient.getETag())
                    .content(bytes))
                    .andExpect(status().isCreated());

            // Make a post to replace the current image
            mockMvc.perform(post("/clients/9999/image")
                    .contentType(MediaType.IMAGE_PNG)
                    .header("If-Match", testClient.getETag())
                    .content(bytes))
                    .andExpect(status().isCreated());
        }
    }

    @Test
    public void invalidPost() throws Exception {
        try (InputStream in = new FileInputStream("./../images/default.png")) {
            byte[] bytes = IOUtils.toByteArray(in);

            mockMvc.perform(post("/clients/9998/image")
                    .contentType(MediaType.IMAGE_PNG)
                    .header("If-Match", testClient.getETag())
                    .content(bytes))
                    .andExpect(status().isNotFound());
        }
    }

    @Test
    public void validDelete() throws Exception {
        try (InputStream in = new FileInputStream("./../images/default.png")) {
            byte[] bytes = IOUtils.toByteArray(in);

            mockMvc.perform(post("/clients/9999/image")
                    .contentType(MediaType.IMAGE_PNG)
                    .header("If-Match", testClient.getETag())
                    .content(bytes))
                    .andExpect(status().isCreated());

            mockMvc.perform(delete("/clients/9999/image")
                    .header("If-Match", testClient.getETag()))
                    .andExpect(status().isOk());
        }
    }

    @Test
    public void invalidDelete() throws Exception {
        mockMvc.perform(delete("/clients/9999/image")
                .header("If-Match", testClient.getETag()))
                .andExpect(status().isNotFound());
    }

}
