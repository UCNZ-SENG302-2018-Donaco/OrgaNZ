package com.humanharvest.organz.server.client;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.server.Application;
import com.humanharvest.organz.state.AuthenticationManager;
import com.humanharvest.organz.state.AuthenticationManagerFake;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import java.nio.charset.Charset;
import java.time.LocalDate;
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
public class ClientIllnessControllerTest {

  private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON
      .getSubtype(), Charset.forName("utf8"));

  private MockMvc mockMvc;
  private Client testClient;
  private String validProcedureJson;
  private String otherJson;
  private String VALID_AUTH = "valid auth";
  private String INVALID_AUTH = "invalid auth";


  IllnessRecord record1 = new IllnessRecord("Tuberculosis", LocalDate.of(2018,04,02),
      null,false);
  IllnessRecord record2 = new IllnessRecord("Influenza", LocalDate.of(2015,02,02),
      LocalDate.of(2015,06,06),false);


  @Autowired
  WebApplicationContext webApplicationContext;

  @Before
  public void init(){
    State.reset();
    State.setAuthenticationManager(new AuthenticationManagerFake());
    this.mockMvc = webAppContextSetup(webApplicationContext).build();
    testClient = new Client("Tom", "Middle", "Last", LocalDate.now(), 1);
    State.getClientManager().addClient(testClient);
    testClient.addIllnessRecord(record1);
    //testClient.addIllnessRecord(record2);
    AuthenticationManager mockAuthenticationManager = mock(AuthenticationManager.class);
    State.setAuthenticationManager(mockAuthenticationManager);
    doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
        .when(mockAuthenticationManager).verifyClinicianOrAdmin(INVALID_AUTH);
    doThrow(new AuthenticationException("X-Auth-Token does not match any allowed user type"))
        .when(mockAuthenticationManager).verifyClinicianOrAdmin(null);
    doNothing().when(mockAuthenticationManager).verifyClinicianOrAdmin(VALID_AUTH);

    validProcedureJson = " {\n"
        + "        \"illnessName\": \"Uncured Tuberculosis\",\n"
        + "        \"diagnosisDate\": \"2018-01-01\",\n"
        + "        \"curedDate\": null,\n"
        + "        \"chronic\": false\n"
        + "    }";

  }

  @Test
  public void getClientsIllnesses() throws Exception{
    mockMvc.perform(get("/clients/1/illnesses"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
        //.andExpect(jsonPath("$.illnessName", is("Tuberculosis")));


  }

  @Test
  public void patchClientIllness() throws Exception {
    String patch = "{\n"
        + "\t\"illnessName\":\"Patchy\",\n"
        + "\t\"diagnosisDate\":\"2018-01-01\"\n"
        + "}";
    mockMvc.perform(patch("/clinicians/0")
        .contentType(contentType)
        .content(patch))
        .andExpect(status().isOk());
  }
  @Test
  public void addValidIllness() throws Exception{

    mockMvc.perform(post("/clients/1/illnesses")
        .contentType(contentType)
        .content(validProcedureJson))
        .andExpect(status().isCreated());

  }

  @Test
  public void deleteIllness() throws Exception {
    testClient.addIllnessRecord(record1);
    mockMvc.perform(delete("/clients/1/illnesses/2"))
        .andExpect(status().isOk());
  }

  @Test
  public void deleteNonExistingIllness() throws Exception {
    mockMvc.perform(delete("/clients/illnesses/10"))
        .andExpect(status().isNotFound());
  }


  @Test
  public void nonExistingClient() throws Exception {
    mockMvc.perform(get("/clients/99"))
        .andExpect(status().isNotFound());
  }

}
