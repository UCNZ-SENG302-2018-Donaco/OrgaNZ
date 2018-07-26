package com.humanharvest.organz.server;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.util.EnumSet;

import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import org.junit.Assert;
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
public class ConfigControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
            .getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;
    private String VALID_AUTH = "valid auth";
    private String INVALID_AUTH = "invalid auth";

    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void init() {
        State.reset();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        // Create mock authentication manager that verifies all clinicianOrAdmins
        AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
        State.setAuthenticationManager(mockAuthenticationManager);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyAdminAccess(INVALID_AUTH);
        doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
                .when(mockAuthenticationManager).verifyAdminAccess(null);
        doNothing().when(mockAuthenticationManager).verifyAdminAccess(VALID_AUTH);
    }

    // ----------------------GET allowed countries------------------------

    @Test
    public void getCountries() throws Exception {
        mockMvc.perform(get("/config/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ----------------------POST allowed countries-----------------------

    @Test
    public void postCountriesOne() throws Exception {
        String json = "[ \"NL\" ]";

        mockMvc.perform(post("/config/countries")
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated());

        EnumSet<Country> expectedCountries = EnumSet.noneOf(Country.class);
        expectedCountries.add(Country.NL);
        Assert.assertEquals(expectedCountries, State.getConfigManager().getAllowedCountries());
    }

    @Test
    public void postCountriesMultiple() throws Exception {
        String json = "[ \"NL\", \"DE\", \"QA\"]";

        mockMvc.perform(post("/config/countries")
                .header("X-Auth-Token", VALID_AUTH)
                .contentType(contentType)
                .content(json))
                .andExpect(status().isCreated());

        EnumSet<Country> expectedCountries = EnumSet.noneOf(Country.class);
        expectedCountries.add(Country.NL);
        expectedCountries.add(Country.DE);
        expectedCountries.add(Country.QA);
        Assert.assertEquals(expectedCountries, State.getConfigManager().getAllowedCountries());
    }

    @Test
    public void postCountriesInvalidAuth() throws Exception {
        String json = "[ \"NL\", \"DE\", \"QA\"]";

        mockMvc.perform(post("/config/countries")
                .header("X-Auth-Token", INVALID_AUTH)
                .contentType(contentType)
                .content(json))
                .andExpect(status().isUnauthorized());
    }
}
