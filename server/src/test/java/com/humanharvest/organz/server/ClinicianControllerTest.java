package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.AuthenticationManagerFake;
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
public class ClinicianControllerTest {

    private MockMvc mockMvc;
    private Clinician testClinician;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClinician = new Clinician("Shawn", "", "Michaels", "1", Region.UNSPECIFIED.toString(), null,1, "hi");
        State.setAuthenticationManager(new AuthenticationManagerFake());

    }

    //------------GET---------------
    // /clinician endpoints
    @Test
    public void getDefault() throws Exception {
        mockMvc.perform(get("/clinicians"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
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
    public void getDefaultDetails() throws Exception {
        mockMvc.perform(get("/clinicians/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(0)))
                .andExpect(jsonPath("$.firstName", is("Default")))
                .andExpect(jsonPath("$.lastName", is("Clinician")))
                .andExpect(jsonPath("$.middleName", is(Matchers.isEmptyOrNullString())))
                .andExpect(jsonPath("$.workAddress", is("Unspecified")))
                .andExpect(jsonPath("$.password", is("clinician")))
                .andExpect(jsonPath("$.region", is(Matchers.equalToIgnoringCase("UNSPECIFIED"))))
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
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
                .contentType(MediaType.APPLICATION_JSON_UTF8)
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

    //------------PATCH---------------

    // Patching with only 2 params
    @Test
    public void patchValidParams() throws Exception {
        String json = "{\"password\": \"ok\", \"region\": \"AUCKLAND\"}";
        mockMvc.perform(patch("/clinicians/0")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(0)))
                .andExpect(jsonPath("$.firstName", is("Default")))
                .andExpect(jsonPath("$.lastName", is("Clinician")))
                .andExpect(jsonPath("$.middleName", is(Matchers.isEmptyOrNullString())))
                .andExpect(jsonPath("$.workAddress", is("Unspecified")))
                .andExpect(jsonPath("$.password", is("ok")))
                .andExpect(jsonPath("$.region", is(Matchers.equalToIgnoringCase("AUCKLAND"))))
                .andExpect(jsonPath("$.createdOn", Matchers.anything()))
                .andExpect(jsonPath("$.modifiedOn", Matchers.anything()));
    }

    // Patching all params
    @Test
    public void validPatchAll() throws Exception {
        String json = "{\"firstName\": \"jan\",  \"lastName\": \"vincent\",  "
                + "\"middleName\": \"michael\", \"workAddress\": \"my home\",   \"password\": \"ok\", "
                + "\"region\": \"UNSPECIFIED\"}";
        mockMvc.perform(patch("/clinicians/0")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.staffId", is(0)))
                .andExpect(jsonPath("$.firstName", is("jan")))
                .andExpect(jsonPath("$.lastName", is("vincent")))
                .andExpect(jsonPath("$.middleName", is("michael")))
                .andExpect(jsonPath("$.workAddress", is("my home")))
                .andExpect(jsonPath("$.password", is("ok")))
                .andExpect(jsonPath("$.region", is(Matchers.equalToIgnoringCase("UNSPECIFIED"))))
                .andExpect(jsonPath("$.createdOn", Matchers.anything()))
                .andExpect(jsonPath("$.modifiedOn", Matchers.anything()));
    }

    @Test
    public void patchNonExistingClinician() throws Exception {
        String json = "{\"password\": \"ok\", \"region\": \"AUCKLAND\"}";
        mockMvc.perform(patch("/clinicians/200")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(json))
                .andExpect(status().isNotFound());
    }


    //------------DELETE---------------
    @Test
    public void validDelete() throws Exception {
        State.getClinicianManager().addClinician(testClinician);
        mockMvc.perform(delete("/clinicians/1"))
                .andExpect(status().isCreated());
    }

    // The default admin cannot be deleted (this is prevented on the client side anyway).
    @Test
    public void tryDeleteDefaultAdmin() throws Exception {
        mockMvc.perform(delete("/clinicians/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteNonExistingAdmin() throws Exception {
        mockMvc.perform(delete("/clinicians/1"))
                .andExpect(status().isNotFound());
    }
}
