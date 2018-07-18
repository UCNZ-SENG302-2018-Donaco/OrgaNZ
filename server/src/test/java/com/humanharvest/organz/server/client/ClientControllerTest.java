package com.humanharvest.organz.server.client;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.enums.Region;
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
    private Client testClient;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(get("/clients/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void userIsFound() throws Exception {
        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.firstName", is("Jan")));
    }

    /**
     * Check that we are getting all the users supplied.
     * Also checks that we are being supplied the firstName and not the region
     */
    @Test
    public void getTwoClientsTest() throws Exception {
        State.getClientManager().addClient(new Client("Fred", "Bob", "Smith", LocalDate.now(), 3));

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jan")))
                .andExpect(jsonPath("$[0].region").doesNotExist());
    }

    //------------POST---------------

    @Test
    public void createValidClientTest() throws Exception {
        String json = "{ \"firstName\": \"New\", \"lastName\": \"Test\", \"dateOfBirth\": \"1987-01-01\" }";
        mockMvc.perform(post("/clients")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.lastName", is("Test")))
                .andExpect(jsonPath("$.dateOfBirth", is("1987-01-01")))
                .andExpect(jsonPath("$.middleName", is(nullValue())));
    }

    @Test
    public void createInvalidMissingLastNameTest() throws Exception {
        String json = "{ \"firstName\": \"New\", \"dateOfBirth\": \"1987-01-01\" }";
        mockMvc.perform(post("/clients")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    //----------PATCH----------

    @Test
    public void invalidClientPatchTest() throws Exception {
        String json = "{ \"middleName\": \"New\" }";

        mockMvc.perform(patch("/clients/5")
                .content(json)
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void malformedJsonTest() throws Exception {
        String json = "notvalidjson";

        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void validPatchNoIfMatchHeaderTest() throws Exception {
        String json = "{ \"middleName\": \"New\" }";

        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType))
                .andExpect(status().isPreconditionRequired());
    }

    @Test
    public void validPatchInvalidIfMatchHeaderTest() throws Exception {
        String json = "{ \"middleName\": \"New\" }";

        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", "x"))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    public void validPatchMiddleNameTest() throws Exception {
        String json = "{ \"middleName\": \"New\" }";

        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isOk());
    }

    @Test
    public void validPatchTextFieldsTest() throws Exception {
        Client testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        State.getClientManager().addClient(testClient);

        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "\"Fred\"");
        fields.put("middleName", "\"Bob\"");
        fields.put("lastName", "\"Smith\"");
        fields.put("preferredName", "\"Joe\"");
        fields.put("currentAddress", "\"123 Fake St\"");

        StringBuilder json = new StringBuilder("{ ");
        for (Entry<String, String> entry : fields.entrySet()) {
            json.append(String.format("\"%s\": %s, ", entry.getKey(), entry.getValue()));
        }
        //Remove the final comma
        json.deleteCharAt(json.length() - 2);
        json.append("}");

        System.out.println(json.toString());

        mockMvc.perform(patch("/clients/1")
                .content(json.toString())
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isOk());

        //Check that the fields updated correctly

        Client c = State.getClientManager().getClientByID(1).orElse(null);
        //assertEquals("");
        System.out.println(c);
    }

    @Test
    public void patchNonNullableFieldToNullTest() throws Exception {
        String json = "{ \"firstName\": null }";
        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchUidTest() throws Exception {
        String json = "{ \"uid\": 500 }";
        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                //Should just ignore the uid field, so a 200 is expected
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(1)));
    }

    @Test
    public void patchRegionValidTest() throws Exception {
        String json = constructJson("region", "CANTERBURY");
        clientPatch(json, testClient);

        assertEquals(Region.CANTERBURY, testClient.getRegion());
    }

    @Test
    public void patchRegionInvalidTest() throws Exception {
        String json = constructJson("region", "NOTAREGION");
        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchBloodTypeValidTest() throws Exception {
        String json = constructJson("bloodType", "O_NEG");
        clientPatch(json, testClient);

        assertEquals(BloodType.O_NEG, testClient.getBloodType());
    }

    @Test
    public void patchGenderAndGenderIdentityValidTest() throws Exception {
        String json = constructJson("gender", "MALE");
        clientPatch(json, testClient);
        json = constructJson("genderIdentity", "FEMALE");
        clientPatch(json, testClient);

        assertEquals(Gender.MALE, testClient.getGender());
        assertEquals(Gender.FEMALE, testClient.getGenderIdentity());
    }

    @Test
    public void patchHeightAndWeightValidTest() throws Exception {
        String json = constructJson("weight", 50);
        clientPatch(json, testClient);
        json = constructJson("height", 180);
        clientPatch(json, testClient);

        assertEquals(50.0, testClient.getWeight());
        assertEquals(180.0, testClient.getHeight());
    }

    @Test
    public void patchDatesTest() throws Exception {
        String json = constructJson("dateOfBirth", "1987-01-01");
        clientPatch(json, testClient);
        json = constructJson("dateOfDeath", "1997-01-01");
        clientPatch(json, testClient);

        assertEquals(LocalDate.of(1987, 1, 1), testClient.getDateOfBirth());
        assertEquals(LocalDate.of(1997, 1, 1), testClient.getDateOfDeath());
    }

    @Test
    public void patchDateInFutureTest() throws Exception {
        String json = constructJson("dateOfBirth", "3000-01-01");
        mockMvc.perform(patch("/clients/1")
                .content(json)
                .contentType(contentType)
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isBadRequest());
    }

    private void clientPatch(String json, Client client) throws Exception {
        mockMvc.perform(patch("/clients/" + client.getUid())
                .content(json)
                .contentType(contentType)
                .header("If-Match", client.getEtag()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType));
    }

    private String constructJson(String field, Object value) {
        if (value.getClass().equals(String.class)) {
            value = "\"" + value + "\"";
        }
        return String.format("{ \"%s\": %s }", field, value);
    }

    //-------------DELETE---------------

    @Test
    public void invalidClientDeleteTest() throws Exception {
        mockMvc.perform(delete("/clients/5")
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isNotFound());
    }

    /**
     * Test to see if a 428 Precondition Required error is thrown if we do not supply any If-Match header
     */
    @Test
    public void deleteValidClientNoIfMatchHeaderTest() throws Exception {
        mockMvc.perform(delete("/clients/1"))
                .andExpect(status().isPreconditionRequired());
    }

    /**
     * Test to see if a 412 Precondition Failed error is thrown if we supply a non matching If-Match header
     */
    @Test
    public void deleteValidClientInvalidIfMatchHeaderTest() throws Exception {
        mockMvc.perform(delete("/clients/1")
                .header("If-Match", "x"))
                .andExpect(status().isPreconditionFailed());
    }

    /**
     * Perform a valid delete with the correct ETag
     */
    @Test
    public void deleteValidClientTest() throws Exception {
        mockMvc.perform(delete("/clients/1")
                .header("If-Match", testClient.getEtag()))
                .andExpect(status().isOk());

        assertNull(State.getClientManager().getClientByID(1));
    }

}
