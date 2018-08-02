package com.humanharvest.organz.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.resolvers.CommandRunner;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.view.Page;
import com.humanharvest.organz.utilities.view.WindowContext.WindowContextBuilder;
import org.junit.Test;

public class MenuBarControllerAdminTest extends ControllerTest {

    private Administrator testAdmin = new Administrator("username", "password");

    @Override
    @Test
    public void initState() {
        State.reset();
        State.login(testAdmin);
        State.getAdministratorManager().addAdministrator(testAdmin);
        mainController.setWindowContext(new WindowContextBuilder().build());
    }

    @Override
    protected Page getPage() {
        return Page.MENU_BAR;
    }

    @Test
    public void refresh() {
    }

    @Test
    public void testClickLogout() {
        clickOn("#filePrimaryItem");
        clickOn("#logOutItem");
        assertEquals(Page.LANDING, mainController.getCurrentPage());
    }

    @Test
    public void testClickSearchClients() {
        clickOn("#clientPrimaryItem");
        clickOn("#searchClientItem");
        assertEquals(Page.SEARCH, mainController.getCurrentPage());
    }

    @Test
    public void testClickCreateClient() {
        clickOn("#clientPrimaryItem");
        clickOn("#createClientItem");
        assertEquals(Page.CREATE_CLIENT, mainController.getCurrentPage());
    }

    @Test
    public void testClickSearchStaff() {
        clickOn("#staffPrimaryItem");
        clickOn("#staffListItem");
        assertEquals(Page.STAFF_LIST, mainController.getCurrentPage());
    }

    @Test
    public void testClickCreateAdministrator() {
        clickOn("#staffPrimaryItem");
        clickOn("#createAdministratorItem");
        assertEquals(Page.CREATE_ADMINISTRATOR, mainController.getCurrentPage());
    }

    @Test
    public void testClickCreateClinician() {
        clickOn("#staffPrimaryItem");
        clickOn("#createClinicianItem");
        assertEquals(Page.CREATE_CLINICIAN, mainController.getCurrentPage());
    }

    @Test
    public void testClickTransplantRequests() {
        clickOn("#organPrimaryItem");
        clickOn("#transplantRequestsItem");
        assertEquals(Page.TRANSPLANTS, mainController.getCurrentPage());
    }

    @Test
    public void testClickHistory() {
        clickOn("#profilePrimaryItem");
        clickOn("#historyItem");
        assertEquals(Page.HISTORY, mainController.getCurrentPage());
    }

    @Test
    public void testClickCLI() {
        CommandRunner commandRunnerMock = mock(CommandRunner.class);
        try {
            setPrivateField(State.class, "commandRunner", commandRunnerMock);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // TODO: Change this to actual output
        when(commandRunnerMock.execute("help")).thenReturn("Usage: OrgaNZ\nCommands:\nload");

        clickOn("#profilePrimaryItem");
        clickOn("#cliItem");
        assertEquals(Page.COMMAND_LINE, mainController.getCurrentPage());
    }
}