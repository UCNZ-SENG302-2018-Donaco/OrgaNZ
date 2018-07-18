package com.humanharvest.organz.server;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;


import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.State;

import com.humanharvest.organz.utilities.enums.Region;
import org.hamcrest.Matchers;
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
public class ClinicianTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private Clinician testClinician;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClinician = new Clinician("Shawn", "", "Michaels", "1", Region.UNSPECIFIED, 1, "hi");

    }

    //------------GET---------------
    // /clinician endpoints
    @Test
    public void getDefault() throws Exception {
        mockMvc.perform(get("/clinicians"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void getMultiple() throws Exception {
        State.getClinicianManager().addClinician(testClinician);
        mockMvc.perform(get("/clinicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAuth() throws Exception {
        //TODO auth for 401's and 403's
    }



    // /clinician/{staffId} endpoints

    @Test
    public void getDefaultDetails() throws Exception {
        mockMvc.perform(get("/clinicians/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(0)))
                .andExpect(jsonPath("$.firstName", is("admin")))
                .andExpect(jsonPath("$.lastName", is("admin")))
                .andExpect(jsonPath("$.middleName", is(Matchers.isEmptyOrNullString())))
                .andExpect(jsonPath("$.workAddress", is("admin")))
                .andExpect(jsonPath("$.password", is("admin")))
                .andExpect(jsonPath("$.region", is("UNSPECIFIED")))
                .andExpect(jsonPath("$.createdOn", Matchers.anything()))
                .andExpect(jsonPath("$.modifiedOn", Matchers.anything()));
    }

    @Test
    public void nonExistingDetails() throws Exception {
        mockMvc.perform(get("/clinicians/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addedDetailsExist() throws Exception {
        State.getClinicianManager().addClinician(testClinician);
        mockMvc.perform(get("/clinicians/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    @Test
    public void getSpecificAuth() throws Exception {
        //TODO auth for 401's and 403's
    }


    //------------POST---------------

    @Test
    public void createValidClinician() throws Exception {
        String json = "{\"staffId\": 1, \"firstName\": \"jan\",  \"lastName\": \"vincent\",  "
                + "\"middleName\": \"michael\", \"workAddress\": \"my home\",   \"password\": \"ok\", "
                + "\"region\": \"UNSPECIFIED\"}";

        mockMvc.perform(post("/clinicians")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdOn", Matchers.notNullValue()))
                .andExpect(jsonPath("modifiedOn", Matchers.notNullValue()));
    }

    @Test
    public void createInvalidClinician() throws Exception { // The json is missing a region
        String json = "{\"staffId\": 1, \"firstName\": \"jan\",  \"lastName\": \"vincent\",  "
                + "\"middleName\": \"michael\", \"workAddress\": \"my home\",   \"password\": \"ok\", ";
        mockMvc.perform(post("/clinicians")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nonAdminCreatingClinician401() throws Exception {
        //TODO auth
    }

    @Test
    public void nonAdminCreatingClinician403() throws Exception {
        //TODO auth
    }
}
