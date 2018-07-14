package com.humanharvest.organz.controller.administrator;

import static org.junit.Assert.assertEquals;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class CreateAdministratorControllerTest extends ControllerTest {

    private Administrator testAdmin = new Administrator("username", "password");

    @Override
    protected Page getPage() {
        return Page.CREATE_ADMINISTRATOR;
    }

    @Override
    protected void initState() {
        State.reset();
        State.login(testAdmin);
        mainController.setWindowContext(WindowContext.defaultContext());
        State.getAdministratorManager().addAdministrator(testAdmin);
        // The administrator manager should now have two admins: default and test.
    }

    @Test
    public void createValidAdminTest() {
        clickOn("#usernameTextField").write("a");
        clickOn("#passwordField").write("b");
        clickOn("#createButton");
        assertEquals(3, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.SEARCH, mainController.getCurrentPage());
    }

    @Test
    public void emptyUsernameTest() {
        clickOn("#passwordField").write("b");
        clickOn("#createButton");
        assertEquals(2, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }

    @Test
    public void emptyPasswordTest() {
        clickOn("#usernameTextField").write("a");
        clickOn("#createButton");
        assertEquals(2, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }

    @Test
    public void numericIntegerUsernameTest() {
        clickOn("#usernameTextField").write("25");
        clickOn("#passwordField").write("b");
        clickOn("#createButton");
        press(KeyCode.ENTER); // Close the popup window
        release(KeyCode.ENTER);
        assertEquals(2, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }

    @Test
    public void numericNonIntegerUsernameTest() {
        clickOn("#usernameTextField").write("3.141");
        clickOn("#passwordField").write("b");
        clickOn("#createButton");
        assertEquals(3, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.SEARCH, mainController.getCurrentPage());
    }

    @Test
    public void takenUsernameTest() {
        clickOn("#usernameTextField").write("username");
        clickOn("#passwordField").write("b");
        clickOn("#createButton");
        press(KeyCode.ENTER); // Close the popup window
        release(KeyCode.ENTER);
        assertEquals(2, State.getAdministratorManager().getAdministrators().size());
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }
}
