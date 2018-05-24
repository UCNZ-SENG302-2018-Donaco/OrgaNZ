package com.humanharvest.organz.state;

import java.util.ArrayList;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.MainController;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    private static ClientManager clientManager;
    private static ClinicianManager clinicianManager;
    private static AdministratorManager administratorManager;
    private static ActionInvoker actionInvoker;
    private static Session session;
    private static boolean unsavedChanges = false;
    private static ArrayList<MainController> mainControllers = new ArrayList<>();

    private State() {
    }

    /**
     * Initialises a new action invoker, client manager and clinician manager.
     */
    public static void init() {
        actionInvoker = new ActionInvoker();
        clientManager = new ClientManagerDBPure();
        clinicianManager = new ClinicianManagerDBPure();
        administratorManager = new AdministratorManagerDBPure();
    }

    private static void init(boolean isDB) {
        actionInvoker = new ActionInvoker();
        if (isDB) {
            clientManager = new ClientManagerDBPure();
            clinicianManager = new ClinicianManagerDBPure();
            administratorManager = new AdministratorManagerDBPure();
        } else {
            clientManager = new ClientManagerMemory();
            clinicianManager = new ClinicianManagerMemory();
            administratorManager = new AdministratorManagerMemory();
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


    public static void reset(boolean isDB) {
        init(isDB);
        logout();
        unsavedChanges = false;
        mainControllers = new ArrayList<>();
    }

    public static void addMainController(MainController mainController) {
        mainControllers.add(mainController);
    }

    public static ArrayList<MainController> getMainControllers() {
        return mainControllers;
    }
}
