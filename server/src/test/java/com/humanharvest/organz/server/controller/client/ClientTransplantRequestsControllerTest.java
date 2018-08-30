package com.humanharvest.organz.server.controller.client;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;

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

    @Autowired
    WebApplicationContext webApplicationContext;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));
    private MockMvc mockMvc;
    private Client testClient;
    private long id;
    private String validTransplantRequestJson;
    private String VALID_AUTH = "valid auth";
    private String INVALID_AUTH = "invalid auth";
    private LocalDateTime justAfterCreatedTime;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        testClient = new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1);
        testClient.setRegion("Auckland");
        TransplantRequest testTransplantRequest = new TransplantRequest(testClient, Organ.LIVER);
        justAfterCreatedTime = LocalDateTime.now();
        testClient.addTransplantRequest(testTransplantRequest);

        State.getClientManager().applyChangesTo(testClient);

        id = testTransplantRequest.getId();

        assertEquals(testTransplantRequest, testClient.getTransplantRequestById(id).orElseThrow
                (NullPointerException::new));
        State.getClientManager().addClient(testClient);

        // Create mock authentication manager that verifies all clinicianOrAdmins
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        State.setAuthenticationManager(mockAuthenticationManager);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(INVALID_AUTH);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyClinicianOrAdmin(null);
        doNothing().when(mockAuthenticationManager).verifyClinicianOrAdmin(VALID_AUTH);

        validTransplantRequestJson = "{\n"
                + "  \"requestedOrgan\": \"LIVER\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";

    }

    //------------GET---------------

    @Test
    public void getAllTransplantRequestsTest() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests").header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(3)));
    }

    @Test
    public void filterByRegionTest() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?regions=Auckland").header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(3)));
    }

    @Test
    public void filterByRegionTestNull() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?regions=Christchurch").header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(0)));
    }

    @Test
    public void filterByOrganTest() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?organs=LIVER").header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(1)));
    }

    @Test
    public void filterByOrganTest2() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?organs=LIVER,HEART,CORNEA").header("X-Auth-Token",
                VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(3)));
    }

    @Test
    public void filterByOrganTestNull() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?organs=KIDNEY").header("X-Auth-Token", VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(0)));
    }

    @Test
    public void filterByOrganAndRegionTest() throws Exception {
        setUpTransplantRequests();

        mockMvc.perform(get("/clients/transplantRequests?organs=HEART&regions=Auckland").header("X-Auth-Token",
                VALID_AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults", is(1)));
    }

    //------------POST---------------

    @Test
    public void createValidTransplantRequestTest() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[1].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[1].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[1].resolvedDate", is("2017-07-19T14:11:20.202")))
                .andExpect(jsonPath("$[1].status", is("WAITING")))
                .andExpect(jsonPath("$[1].resolvedReason", is("reason")));
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
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[1].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[1].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[1].resolvedDate", is("2017-07-19T14:11:20.202")))
                .andExpect(jsonPath("$[1].status", is("WAITING")))
                .andExpect(jsonPath("$[1].resolvedReason", isEmptyOrNullString()));
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
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[1].requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$[1].requestDate", is("2017-07-18T14:11:20.202")))
                .andExpect(jsonPath("$[1].resolvedDate", isEmptyOrNullString()))
                .andExpect(jsonPath("$[1].status", is("WAITING")))
                .andExpect(jsonPath("$[1].resolvedReason", is("reason")));
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
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
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
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
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
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTransplantRequestInvalidClientId() throws Exception {
        int invalidId = testClient.getUid() + 1;
        mockMvc.perform(post("/clients/" + invalidId + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createTransplantRequestInvalidNoEtag() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().is(428));
    }

    @Test
    public void createTransplantRequestInvalidEtag() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag() + "X")
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().is(412));
    }

    @Test
    public void createTransplantRequestInvalidNoAuth() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createTransplantRequestInvalidAuth() throws Exception {
        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", INVALID_AUTH)
                .contentType(contentType)
                .content(validTransplantRequestJson))
                .andExpect(status().isUnauthorized());
    }

    //------------PATCH---------------

    @Test
    public void patchValidTransplantRequestTest() throws Exception {

        //Get the time after created and trim trailing zero as server doesn't return it
        String localDateTimeString = justAfterCreatedTime.toString();
        localDateTimeString = localDateTimeString.replaceAll("0+$", "");

        // Perform patch
        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/transplantRequests/" + id)
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content("{\n"
                        + "  \"resolvedDate\": \"" + localDateTimeString + "\",\n"
                        + "  \"status\": \"CANCELLED\",\n"
                        + "  \"resolvedReason\": \"it was a mistake\"\n"
                        + "}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.requestedOrgan", is("LIVER")))
                .andExpect(jsonPath("$.resolvedDate", is(localDateTimeString)))
                .andExpect(jsonPath("$.status", is("CANCELLED")))
                .andExpect(jsonPath("$.resolvedReason", is("it was a mistake")));
    }

    @Test
    public void patchInvalidTransplantRequestBadIdTest() throws Exception {

        // Perform patch
        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/transplantRequests/123")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content("{\n"
                        + "  \"resolvedDate\": \"2019-07-19T14:11:20.202\",\n"
                        + "  \"status\": \"CANCELLED\",\n"
                        + "  \"resolvedReason\": \"it was a mistake\"\n"
                        + "}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void patchInvalidTransplantRequestResolveDateBeforeRequestedTest() throws Exception {

        //Get the time after created and trim trailing zero as server doesn't return it
        String localDateTimeString = justAfterCreatedTime.minusMinutes(1).toString();
        localDateTimeString = localDateTimeString.replaceAll("0+$", "");

        // Perform patch
        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/transplantRequests/" + id)
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content("{\n"
                        + "  \"resolvedDate\": \"" + localDateTimeString + "\",\n"
                        + "  \"status\": \"CANCELLED\",\n"
                        + "  \"resolvedReason\": \"it was a mistake\"\n"
                        + "}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void patchInvalidTransplantRequestUnauthorisedTest() throws Exception {
        // Perform patch
        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/transplantRequests/" + id)
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", INVALID_AUTH)
                .contentType(contentType)
                .content("{\n"
                        + "  \"resolvedDate\": \"2019-07-19T14:11:20.202\",\n"
                        + "  \"status\": \"CANCELLED\",\n"
                        + "  \"resolvedReason\": \"it was a mistake\"\n"
                        + "}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void patchInvalidTransplantRequestBadEtagTest() throws Exception {

        // Perform patch
        mockMvc.perform(patch("/clients/" + testClient.getUid() + "/transplantRequests/" + id)
                .header("If-Match", testClient.getETag() + "X")
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content("{\n"
                        + "  \"resolvedDate\": \"2019-07-19T14:11:20.202\",\n"
                        + "  \"status\": \"CANCELLED\",\n"
                        + "  \"resolvedReason\": \"it was a mistake\"\n"
                        + "}"))
                .andExpect(status().is(412));
    }

    /**
     * Creates two extra transplant requests.
     */
    private void setUpTransplantRequests() throws Exception {
        String anotherValidTransplantRequestJson = "{\n"
                + "  \"requestedOrgan\": \"HEART\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";

        String oneMoreValidTransplantRequestJson = "{\n"
                + "  \"requestedOrgan\": \"CORNEA\",\n"
                + "  \"requestDate\": \"2017-07-18T14:11:20.202\",\n"
                + "  \"resolvedDate\": \"2017-07-19T14:11:20.202\",\n"
                + "  \"status\": \"WAITING\",\n"
                + "  \"resolvedReason\": \"reason\"\n"
                + "}";

        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(anotherValidTransplantRequestJson));

        mockMvc.perform(post("/clients/" + testClient.getUid() + "/transplantRequests")
                .header("If-Match", testClient.getETag())
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(oneMoreValidTransplantRequestJson));
    }
}