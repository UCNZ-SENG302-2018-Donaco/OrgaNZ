package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
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

    private static final MediaType jsonContentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private ResultActions lastAction;
    private String etag;
    private String xAuthToken;

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    public CucumberSteps() {
        Before(this::stepSetup);

        CreateSharedGiven();
        CreateClientGiven();
        CreateAdministratorGiven();

        CreateSharedWhen();
        CreateClientWhen();
        CreateAdministratorWhen();

        CreateSharedThen();
        CreateClientThen();
        CreateAdministratorThen();
    }

    private void CreateSharedGiven() {
        Given("^I have an etag of value (x)$", (String etagValue) -> {
            etag = etagValue;
        });

        Given("^authentication is required$", () -> {
            State.setAuthenticationManager(new AuthenticationManager());
        });
    }

    private void CreateSharedWhen() {
        When("^I get (.+)$", (String url) -> {
            MockHttpServletRequestBuilder request = get(url);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I post to (.+?) using (.+)$", (String url, String json) -> {
            MockHttpServletRequestBuilder request = post(url)
                    .contentType(jsonContentType)
                    .content(json);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I patch to (.+?) using (.+)$", (String url, String json) -> {
            MockHttpServletRequestBuilder request = patch(url)
                    .content(json)
                    .contentType(jsonContentType);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });

        When("^I delete (.+)$", (String url) -> {
            MockHttpServletRequestBuilder request = delete(url);
            request = setupSharedHeaders(request);
            lastAction = mockMvc.perform(request);
        });
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

    private void CreateSharedThen() {
        Then("^the result is ok", () -> {
            lastAction = lastAction.andExpect(status().isOk());
        });

        Then("^the result is not found", () -> {
            lastAction = lastAction.andExpect(status().isNotFound());
        });

        Then("^the content type is json$", () -> {
            lastAction = lastAction.andExpect(content().contentType(jsonContentType));
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

    private void CreateClientGiven() {
        Given("^there is a test client named (\\w+) (\\w+) (\\w+)$",
                (String firstName, String middleName, String lastName) -> {
                    Client testClient = new Client(firstName,
                            middleName,
                            lastName,
                            LocalDate.now(),
                            State.getClientManager().nextUid());
                    State.getClientManager().addClient(testClient);
                });

        Given("^there is a test client$", () -> {
            Client testClient = new Client("Jan",
                    "Micheal",
                    "Vincent",
                    LocalDate.now(),
                    State.getClientManager().nextUid());
            State.getClientManager().addClient(testClient);
        });

        Given("^I have an etag from client (\\d+)$", (Integer clientId) -> {
            etag = State.getClientManager().getClientByID(clientId).getEtag();
        });
    }

    private void CreateClientWhen() {
    }

    private void CreateClientThen() {
    }

    private void CreateAdministratorGiven() {
        Given("^there is a test administrator with the username (\\w+) and password (\\w+)$",
                (String username, String password) -> {
                    Administrator administrator = new Administrator(username, password);
                    State.getAdministratorManager().addAdministrator(administrator);
                });

        Given("^the authentication token is from administrator (\\w+)", (String username) -> {
            xAuthToken = State.getAuthenticationManager().generateAdministratorToken(username);
        });
    }

    private void CreateAdministratorWhen() {
    }

    private void CreateAdministratorThen() {
    }

    private void stepSetup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        State.reset();
        State.setAuthenticationManager(new AuthenticationManagerFake());
        lastAction = null;
        etag = null;
    }
}
