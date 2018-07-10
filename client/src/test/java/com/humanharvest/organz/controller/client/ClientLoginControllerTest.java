package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import javafx.scene.Node;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class ClientLoginControllerTest extends ControllerTest {
    Client testClient = new Client("test", "", "er", LocalDate.now(), 1);

    @Override
    protected Page getPage() {
        return Page.LOGIN_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset(false);
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void noSelectLogin() {
        clickOn("Log in");
        assertEquals(Page.LOGIN_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void validLogin() {
        clickOn((Node) lookup(".list-cell").nth(0).query());
        clickOn("Log in");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void goBackButtontest() {
        clickOn("Back");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

}