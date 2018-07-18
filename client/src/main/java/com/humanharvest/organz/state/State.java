package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.utilities.RestErrorHandler;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    public enum DataStorageType {
        MEMORY, REST
    }

    public static final String BASE_URI = "http://localhost:8080/";

    private static DataStorageType currentStorageType = DataStorageType.MEMORY;

    private static ClientManager clientManager;
    private static ClinicianManager clinicianManager;
    private static AdministratorManager administratorManager;
    private static AuthenticationManager authenticationManager;

    private static ActionInvoker actionInvoker;
    private static Session session;
    private static boolean unsavedChanges = false;
    private static List<MainController> mainControllers = new ArrayList<>();
    private static RestTemplate restTemplate = new RestTemplate();
    private static String clientEtag = "";
    private static String clinicianEtag = "";

    public static String getClientEtag() {
        return clientEtag;
    }

    public static void setClientEtag(String etag) {
        clientEtag = etag;
    }

    public static void setClinicianEtag(String etag) {
        clinicianEtag = etag;
    }

    private State() {
    }

    /**
     * Initialises a new action invoker, client manager and clinician manager.
     */
    public static void init(DataStorageType storageType) {
        actionInvoker = new ActionInvoker();
        currentStorageType = storageType;

        if (storageType == DataStorageType.REST) {
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            restTemplate.setRequestFactory(requestFactory);
            restTemplate.setErrorHandler(new RestErrorHandler());
            restTemplate.getMessageConverters().removeIf(m -> m.getClass().getName().equals
                    (MappingJackson2HttpMessageConverter.class.getName()));
            restTemplate.getMessageConverters().add(customConverter());


            clientManager = new ClientManagerRest();
            clinicianManager = new ClinicianManagerRest();
            administratorManager = new AdministratorManagerRest();
            authenticationManager = new AuthenticationManagerRest();
        } else if (storageType == DataStorageType.MEMORY) {
            clientManager = new ClientManagerMemory();
            clinicianManager = new ClinicianManagerMemory();
            administratorManager = new AdministratorManagerMemory();
            authenticationManager = new AuthenticationManagerMemory();
        } else {
            throw new IllegalArgumentException("DataStorageType cannot be null.");
        }
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

    public static AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
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
        mainControllers = new ArrayList<>();
    }

    public static void addMainController(MainController mainController) {
        mainControllers.add(mainController);
    }

    public static List<MainController> getMainControllers() {
        return mainControllers;
    }

    private static MappingJackson2HttpMessageConverter customConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(customObjectMapper());
        return converter;
    }

    public static ObjectMapper customObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        return mapper;
    }
}
