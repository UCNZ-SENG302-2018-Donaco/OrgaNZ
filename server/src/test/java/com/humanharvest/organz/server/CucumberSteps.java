package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.time.LocalDate;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.State;
import cucumber.api.PendingException;
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

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    public CucumberSteps() {
        Before(() -> {
            mockMvc = webAppContextSetup(webApplicationContext).build();
            State.reset(false);
            lastAction = null;
            etag = null;
        });

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

        Given("^I have an etag of value (x)$", (String etagValue) -> {
            etag = etagValue;
        });

        When("^I get client (\\d+)$", (Integer clientId) -> {
            lastAction = mockMvc.perform(get("/clients/" + clientId));
        });

        When("^I get the clients$", () -> {
            lastAction = mockMvc.perform(get("/clients/"));
        });

        When("^I create a client using (.+)$", (String json) -> {
            lastAction = mockMvc.perform(post("/clients/")
                    .contentType(jsonContentType)
                    .content(json));
        });

        When("^I update client (\\d+) using (.+)$", (Integer clientId, String json) -> {
            MockHttpServletRequestBuilder patchQuery = patch(String.format("/clients/%d", clientId))
                    .content(json)
                    .contentType(jsonContentType);

            if (etag != null) {
                patchQuery = patchQuery.header("If-Match", etag);
            }

            lastAction = mockMvc.perform(patchQuery);
        });

        When("^I delete client (\\d+)$", (Integer clientId) -> {
            MockHttpServletRequestBuilder deleteQuery = delete(String.format("/clients/%d", clientId));

            if (etag != null) {
                deleteQuery = deleteQuery.header("If-Match", etag);
            }

            lastAction = mockMvc.perform(deleteQuery);
        });

        Then("^the result is ok", () -> {
            lastAction = lastAction.andExpect(status().isOk());
        });

        Then("^the result is not found", () -> {
            lastAction = lastAction.andExpect(status().isNotFound());
        });

        Then("^the content type is json$", () -> {
            lastAction = lastAction.andExpect(content().contentType(jsonContentType));
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
            lastAction = lastAction.andExpect(jsonPath("$", hasSize(2)));
        });

        Then("^client (\\d+)'s (\\w+) is (\\w+)$", (Integer clientId, String fieldName, String firstName) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$[%d].%s", clientId, fieldName), is(firstName)));
        });

        Then("^client (\\d+)'s (\\w+) does not exist$", (Integer clientId, String fieldName) -> {
            lastAction = lastAction.andExpect(jsonPath(String.format("$[%d].%s", clientId, fieldName)).doesNotExist());
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
    }
}
