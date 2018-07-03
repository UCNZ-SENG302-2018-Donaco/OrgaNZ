package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpRequestFactory;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.controller.MainController;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    public enum DataStorageType {
        MEMORY, PUREDB
    }

    private static ClientManager clientManager;
    private static ClinicianManager clinicianManager;
    private static AdministratorManager administratorManager;
    private static ActionInvoker actionInvoker;
    private static Session session;
    private static boolean unsavedChanges = false;
    private static List<MainController> mainControllers = new ArrayList<>();
    private static RestTemplate restTemplate = new RestTemplate();
    private static String clientEtag = "";

    public static String getClientEtag() {
        return clientEtag;
    }

    public static void setClientEtag(String etag) {
        clientEtag = etag;
    }

    private State() {
    }

    /**
     * Initialises a new action invoker, client manager and clinician manager.
     */
    public static void init(DataStorageType storageType) {

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);

        actionInvoker = new ActionInvoker();


        clientManager = new ClientManagerRest();
        clinicianManager = new ClinicianManagerRest();
        administratorManager = new AdministratorManagerRest();


//        if (storageType == DataStorageType.PUREDB) {
//            clientManager = new ClientManagerDBPure();
//            clinicianManager = new ClinicianManagerDBPure();
//            administratorManager = new AdministratorManagerDBPure();
//        } else if (storageType == DataStorageType.MEMORY) {
//            clientManager = new ClientManagerMemory();
//            clinicianManager = new ClinicianManagerMemory();
//            administratorManager = new AdministratorManagerMemory();
//        } else {
//            throw new IllegalArgumentException("DataStorageType cannot be null.");
//        }
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
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
        if (isDB) {
            init(DataStorageType.PUREDB);
        } else {
            init(DataStorageType.MEMORY);
        }
        logout();
        unsavedChanges = false;
        mainControllers = new ArrayList<>();
    }

    public static void addMainController(MainController mainController) {
        mainControllers.add(mainController);
    }

    public static List<MainController> getMainControllers() {
        return mainControllers;
    }
}
