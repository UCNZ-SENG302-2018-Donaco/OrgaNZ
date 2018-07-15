package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;

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
     */
    public static void init(DataStorageType storageType) {
        actionInvoker = new ActionInvoker();

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
    }

    public static void login(Clinician clinician) {
        session = new Session(clinician);
    }

    public static void login(Administrator administrator) {
        session = new Session(administrator);
    }

    public static void setUnsavedChanges(boolean changes) {
        unsavedChanges = changes;
    }

    public static boolean isUnsavedChanges() {
        return unsavedChanges;
    }

    public static void logout() {
        // Do something with the old session
        session = null;
    }

    public static void reset() {
        init(currentStorageType);
        logout();
        unsavedChanges = false;
    }
}
