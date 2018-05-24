package seng302.Controller.Client;

import org.junit.Test;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import static org.junit.Assert.*;

public class CreateClientControllerTest extends ControllerTest {


    @Override
    protected Page getPage() {
        return Page.CREATE_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset(false);
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