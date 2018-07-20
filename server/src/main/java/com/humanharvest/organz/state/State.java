package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.utilities.update_listener.ActionOccurredListener;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    public enum DataStorageType {
        MEMORY, PUREDB
    }

    private static DataStorageType currentStorageType = DataStorageType.MEMORY;

    private static ClientManager clientManager;
    private static ClinicianManager clinicianManager;
    private static AdministratorManager administratorManager;
    private static AuthenticationManager authenticationManager;
    private static ActionInvoker actionInvoker;
    private static Session session;
    private static boolean unsavedChanges = false;

    private State() {
    }

    /**
     * Initialises a new action invoker, client manager and clinician manager.
     * Also binds an ActionOccurredListener to the new ActionInvoker
     */
    public static void init(DataStorageType storageType) {
        actionInvoker = new ActionInvoker();
        registerActionOccurredListener(actionInvoker);

        currentStorageType = storageType;

        authenticationManager = new AuthenticationManager();
        if (storageType == DataStorageType.PUREDB) {
            clientManager = new ClientManagerDBPure();
            clinicianManager = new ClinicianManagerDBPure();
            administratorManager = new AdministratorManagerDBPure();
        } else if (storageType == DataStorageType.MEMORY) {
            clientManager = new ClientManagerMemory();
            clinicianManager = new ClinicianManagerMemory();
            administratorManager = new AdministratorManagerMemory();
        } else {
            throw new IllegalArgumentException("DataStorageType cannot be null.");
        }
    }

    public static ClientManager getClientManager() {
        return clientManager;
    }

    public static ClinicianManager getClinicianManager() {
        return clinicianManager;
    }

    public static AdministratorManager getAdministratorManager() {
        return administratorManager;
    }

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public static void setAuthenticationManager(AuthenticationManager authenticationManager) {
        State.authenticationManager = authenticationManager;
    }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }

    public static Session getSession() {
        return session;
    }

    public static void login(Client client) {
        session = new Session(client);
        HistoryItem historyItem = new HistoryItem("LOGIN", String.format("Client %d (%s) logged in.",
                client.getUid(), client.getFullName()));
        session.addToSessionHistory(historyItem);
    }

    public static void login(Clinician clinician) {
        session = new Session(clinician);
        HistoryItem historyItem = new HistoryItem("LOGIN", String.format("Clinician %d (%s) logged in.",
                clinician.getStaffId(), clinician.getFullName()));
        session.addToSessionHistory(historyItem);
    }

    public static void login(Administrator administrator) {
        session = new Session(administrator);
        HistoryItem historyItem = new HistoryItem("LOGIN", String.format("Administrator %s logged in.",
                administrator.getUsername()));
        session.addToSessionHistory(historyItem);
    }

    public static boolean isUnsavedChanges() {
        return unsavedUpdates != 0;
    }

    public static void logout() {
        HistoryItem historyItem = new HistoryItem("LOGOUT", "The user logged out.");
        session.addToSessionHistory(historyItem);
        // Do something with the old session
        session = null;
    }

    public static void reset() {
        init(currentStorageType);
        logout();
        unsavedChanges = false;
    }

    public static void resetUnsavedUpdates() {
        unsavedUpdates = 0;
    }

    /**
     * Create and bind an ActionOccurredListener to the given ActionInvoker
     * @param invoker The invoker to bind to
     */
    private static void registerActionOccurredListener(ActionInvoker invoker) {
        ActionOccurredListener listener = new ActionOccurredListener() {
            @Override
            public void onActionExecuted(Action action) {
                unsavedUpdates++;

                if (getSession() != null) {
                    getSession().addToSessionHistory(action.getExecuteHistoryItem());
                }
            }

            @Override
            public void onActionUndone(Action action) {
                unsavedUpdates--;

                if (getSession() != null) {
                    State.getSession().addToSessionHistory(new HistoryItem(
                            "UNDO",
                            action.getUnexecuteText()
                    ));
                }
            }

            @Override
            public void onActionRedone(Action action) {
                unsavedUpdates++;

                if (getSession() != null) {
                    State.getSession().addToSessionHistory(new HistoryItem(
                            "REDO",
                            action.getExecuteText()
                    ));
                }
            }
        };
        invoker.registerActionOccuredListener(listener);
    }
}
