package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Test;

public class CreateClientControllerTest extends ControllerTest {


    @Override
    protected Page getPage() {
        return Page.CREATE_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void createValidClient1() {
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
    public void noPassword() {
        clickOn("#firstNameFld").write("a");
        clickOn("#lastNamefld").write("b");
        clickOn("#createButton");
        clickOn("OK");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }
}