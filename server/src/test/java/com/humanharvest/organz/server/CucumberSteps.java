package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.time.LocalDate;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Region;
import cucumber.api.java8.En;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public final class CucumberSteps implements En {

    private ResultActions lastAction;
    private String etag;
    private String xAuthToken;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    public CucumberSteps() {
        Before(this::stepSetup);

        createSharedGiven();
        createClientGiven();
        createClinicianGiven();
        createAdministratorGiven();

        createSharedWhen();
        createClientWhen();
        createClinicianWhen();
        createAdministratorWhen();

        createSharedThen();
        createClientThen();
        createClinicianThen();
        createAdministratorThen();
    }

    private MockHttpServletRequestBuilder setupSharedHeaders(MockHttpServletRequestBuilder request) {
        if (etag != null) {
            request = request.header("If-Match", etag);
        }
        if (xAuthToken != null) {
            request = request.header("x-Auth-Token", xAuthToken);
        }
        return request;
    }

    private void createSharedGiven() {
        Given("^I have an etag of value (x)$", (String etagValue) -> {
            etag = etagValue;
        });

        Given("^authentication is required$", () -> {
            State.setAuthenticationManager(new AuthenticationManager());
        });
    }

    private void createSharedWhen() {
        When("^I get (.+)$", (String url) -> {
            MockHttpServletRequestBuilder request = get(url);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I post to (.+?) using (.+)$", (String url, String json) -> {
            MockHttpServletRequestBuilder request = post(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(json);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I patch to (.+?) using (.+)$", (String url, String json) -> {
            MockHttpServletRequestBuilder request = patch(url)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON_UTF8);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I delete (.+)$", (String url) -> {
            MockHttpServletRequestBuilder request = delete(url);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });
    }

    private void createSharedThen() {
        Then("^the result is ok", () -> {
            lastAction = lastAction.andExpect(status().isOk());
        });

        Then("^the result is not found", () -> {
            lastAction = lastAction.andExpect(status().isNotFound());
        });

        Then("^the content type is json$", () -> {
            lastAction = lastAction.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        });

        Then("^the field (\\w+) exists$", (String fieldName) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$.%s", fieldName), anything()));
        });

        Then("^the field (\\w+) is (\\d+)$", (String fieldName, Integer value) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$.%s", fieldName), is(value)));
        });

        Then("^the field (\\w+) is ((?:[a-zA-Z_][a-zA-Z0-9_]*)|\"[^\"]+?\")$", (String fieldName, String value) -> {
            String s = value;
            if ("null".equals(s)) {
                lastAction = lastAction.andExpect(jsonPath(String.format("$.%s", fieldName), is(nullValue())));
            } else {
                if (!s.isEmpty() && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    s = s.substring(1, s.length() - 1);
                }

                lastAction = lastAction.andExpect(jsonPath(String.format("$.%s", fieldName), is(s)));
            }
        });

        Then("^the result has (\\d+) elements$", (Integer size) -> {
            lastAction = lastAction.andExpect(jsonPath("$", hasSize(size)));
        });

        Then("^result (\\d+)'s (\\w+) is (\\w+)$", (Integer index, String fieldName, String firstName) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$[%d].%s", index, fieldName), is(firstName)));
        });

        Then("^result (\\d+)'s (\\w+) does not exist$", (Integer index, String fieldName) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$[%d].%s", index, fieldName)).doesNotExist());
        });

        Then("^the result is created$", () -> {
            lastAction = lastAction.andExpect(status().isCreated());
        });

        Then("^the result is bad request$", () -> {
            lastAction = lastAction.andExpect(status().isBadRequest());
        });

        Then("^the result is precondition required$", () -> {
            lastAction = lastAction.andExpect(status().isPreconditionRequired());
        });

        Then("^the result is precondition failed$", () -> {
            lastAction = lastAction.andExpect(status().isPreconditionFailed());
        });

        Then("^the result is unauthenticated$", () -> {
            lastAction = lastAction.andExpect(status().isUnauthorized());
        });
    }

    private void createClientGiven() {
        Given("^there is a test client named (\\w+) (\\w+) (\\w+)$",
                (String firstName, String middleName, String lastName) -> {
                    Client testClient = new Client(firstName,
                            middleName,
                            lastName,
                            LocalDate.now(),
                            null);
                    State.getClientManager().addClient(testClient);
                });

        Given("^there is a test client$", () -> {
            Client testClient = new Client("Jan",
                    "Micheal",
                    "Vincent",
                    LocalDate.now(),
                    null);
            State.getClientManager().addClient(testClient);
        });

        Given("^I have an etag from client (\\d+)$", (Integer clientId) -> {
            etag = State.getClientManager()
                    .getClientByID(clientId)
                    .orElseThrow(IllegalArgumentException::new)
                    .getETag();
        });
    }

    private void createClientWhen() {
    }

    private void createClientThen() {
    }

    private void createClinicianGiven() {
        Given("^there is a test clinician with the staff-id (\\d+) and password (\\w+)$",
                (Integer staffId, String password) -> {
                    Clinician clinician = new Clinician("test",
                            "test",
                            "test",
                            "test",
                            Region.UNSPECIFIED,
                            staffId,
                            password);
                    State.getClinicianManager().addClinician(clinician);
                });

        Given("^the authentication token is from clinician (\\d+)", (Integer staffId) -> {
            xAuthToken = State.getAuthenticationManager().generateClinicianToken(staffId);
        });
    }

    private void createClinicianWhen() {
    }

    private void createClinicianThen() {
    }

    private void createAdministratorGiven() {
        Given("^there is a test administrator with the username (\\w+) and password (\\w+)$",
                (String username, String password) -> {
                    Administrator administrator = new Administrator(username, password);
                    State.getAdministratorManager().addAdministrator(administrator);
                });

        Given("^the authentication token is from administrator (\\w+)", (String username) -> {
            xAuthToken = State.getAuthenticationManager().generateAdministratorToken(username);
        });

        Given("^there is a test administrator$", () -> {
            Administrator administrator = new Administrator("test", "test");
            State.getAdministratorManager().addAdministrator(administrator);
        });
        Given("^I have an etag from administrator (\\w+)$", (String username) -> {
            etag = State.getAdministratorManager()
                    .getAdministratorByUsername(username)
                    .orElseThrow(IllegalArgumentException::new)
                    .getETag();
        });
    }

    private void createAdministratorWhen() {
    }

    private void createAdministratorThen() {
    }

    private void stepSetup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        lastAction = null;
        etag = null;
    }
}
