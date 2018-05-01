package seng302.Controller.Client;

import javafx.scene.Node;
import org.junit.Test;
import seng302.Client;
import seng302.Controller.ControllerTest;
import seng302.State.State;
import seng302.Utilities.View.Page;
import seng302.Utilities.View.WindowContext;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class ClientLoginControllerTest extends ControllerTest {
    Client testClient = new Client("test", "", "er", LocalDate.now(), 1);

    @Override
    protected Page getPage() {
        return Page.LOGIN_CLIENT;
    }

    @Override
    protected void initState() {
        State.init();
        State.getClientManager().addClient(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Test
    public void noSelectLogin() {
        clickOn("#signInButton");
        assertEquals(Page.LOGIN_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void validLogin() {
        clickOn((Node) lookup(".list-cell").nth(0).query());
        clickOn("#signInButton");
        assertEquals(Page.VIEW_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void goBackButtontest() {
        clickOn("#goBackButton");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

}