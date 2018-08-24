package com.humanharvest.organz.controller.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class CreateClientControllerTest extends ControllerTest {


    @Override
    protected Page getPage() {
        return Page.CREATE_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();
        mainController.setWindowContext(WindowContext.defaultContext());

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setETag("\"etag\"");

        Client testClient = new Client("First", null, "Last", LocalDate.of(1970, 1, 1), 1);

        ResponseEntity<Client> responseEntity = new ResponseEntity<>(testClient, responseHeaders, HttpStatus
                .CREATED);

        State.getClientManager().addClient(testClient);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Client.class))).thenReturn(responseEntity);
    }

    @Test
    public void createValidClient() {
        clickOn("#firstNameFld").write("a");
        clickOn("#lastNamefld").write("b");
        clickOn("#dobFld").write("01/01/2000");
        clickOn("#createButton");

        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void noFirstName() {
        clickOn("#lastNamefld").write("b");
        clickOn("#dobFld").write("01/01/2000");
        clickOn("#createButton");
        clickOn("OK");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void noLastName() {
        clickOn("#firstNameFld").write("a");
        clickOn("#dobFld").write("01/01/2000");
        clickOn("#createButton");
        clickOn("OK");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void invalidDOB() {
        clickOn("#firstNameFld").write("a");
        clickOn("#lastNamefld").write("b");
        clickOn("#dobFld").write("01/01/3000");
        clickOn("#createButton");
        clickOn("OK");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }
}