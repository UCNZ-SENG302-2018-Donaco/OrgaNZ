package com.humanharvest.organz.controller.client;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import javafx.scene.input.KeyCode;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.controller.ControllerTest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.BloodType;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ViewClientControllerSetTitleTest extends ControllerTest {
    private Client testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);

    @Override
    protected Page getPage() {
        return Page.VIEW_CLIENT;
    }

    @Override
    protected void initState() {
        State.reset();
        setClientDetails();
        State.getClientManager().addClient(testClient);
        State.login(testClient);
        mainController.setWindowContext(WindowContext.defaultContext());
    }

    @Before
    public void setClientDetails() {
        testClient.setFirstName("a");
        testClient.setLastName("b");
        testClient.setDateOfBirth(LocalDate.now().minusDays(10));
        testClient.setBloodType(BloodType.A_POS);
        testClient.setRegion(Region.AUCKLAND);
        testClient.setHeight(180);
        testClient.setWeight(80);
        testClient.setCurrentAddress("1 Test Road");
    }

    @Test
    @Ignore
    public void checkTitleSetTest() {
        testClient = new Client("a", "", "b", LocalDate.now().minusDays(10), 1);
        clickOn("#pname").type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE).write("Dad");
        sleep(1000);
        clickOn("#applyButton");
        assertEquals("View Client: Dad", mainController.getTitle());
    }
}