package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.Config;
import com.humanharvest.organz.controller.MainController;
import com.humanharvest.organz.resolvers.CommandRunner;
import com.humanharvest.organz.resolvers.CommandRunnerRest;
import com.humanharvest.organz.resolvers.actions.ActionResolver;
import com.humanharvest.organz.resolvers.actions.ActionResolverMemory;
import com.humanharvest.organz.resolvers.actions.ActionResolverRest;
import com.humanharvest.organz.resolvers.administrator.AdministratorResolver;
import com.humanharvest.organz.resolvers.administrator.AdministratorResolverMemory;
import com.humanharvest.organz.resolvers.administrator.AdministratorResolverRest;
import com.humanharvest.organz.resolvers.administrator.ClientFileResolver;
import com.humanharvest.organz.resolvers.administrator.ClientFileResolverMemory;
import com.humanharvest.organz.resolvers.administrator.ClientFileResolverRest;
import com.humanharvest.organz.resolvers.client.ClientResolver;
import com.humanharvest.organz.resolvers.client.ClientResolverMemory;
import com.humanharvest.organz.resolvers.client.ClientResolverRest;
import com.humanharvest.organz.resolvers.clinician.ClincianResolverMemory;
import com.humanharvest.organz.resolvers.clinician.ClinicianResolver;
import com.humanharvest.organz.resolvers.clinician.ClinicianResolverRest;
import com.humanharvest.organz.utilities.RestErrorHandler;
import com.humanharvest.organz.utilities.enums.Country;
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
    private static ClientResolver clientResolver;
    private static ClinicianManager clinicianManager;
    private static AdministratorManager administratorManager;
    private static AuthenticationManager authenticationManager;
    private static CommandRunner commandRunner;
    private static ActionResolver actionResolver;
    private static ClinicianResolver clinicianResolver;
    private static AdministratorResolver administratorResolver;
    private static ClientFileResolver clientFileResolver;
    private static ConfigManager configManager;

    private static Session session;
    private static boolean unsavedChanges;
    private static List<MainController> mainControllers = new ArrayList<>();
    private static RestTemplate restTemplate = new RestTemplate();
    private static String clientEtag = "";
    private static String clinicianEtag = "";
    private static String administratorEtag = "";
    private static String token = "";
    private static EnumSet<Country> allowedCountries;

    private State() {
    }

    /**
     * Initialises a new action invoker, client manager and clinician manager.
     */
    public static void init(DataStorageType storageType) {
        currentStorageType = storageType;

        if (storageType == DataStorageType.REST) {
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            restTemplate.setRequestFactory(requestFactory);
            restTemplate.setErrorHandler(new RestErrorHandler());
            restTemplate.getMessageConverters().removeIf(o ->
                    o instanceof MappingJackson2HttpMessageConverter);
            restTemplate.getMessageConverters().add(customConverter());

            clientManager = new ClientManagerRest();
            clientResolver = new ClientResolverRest();
            clinicianManager = new ClinicianManagerRest();
            administratorManager = new AdministratorManagerRest();
            authenticationManager = new AuthenticationManagerRest();
            configManager = new ConfigManagerRest();
            commandRunner = new CommandRunnerRest();
            actionResolver = new ActionResolverRest();
            clinicianResolver = new ClinicianResolverRest();
            administratorResolver = new AdministratorResolverRest();
            clientFileResolver = new ClientFileResolverRest();
        } else if (storageType == DataStorageType.MEMORY) {
            clientManager = new ClientManagerMemory();
            clientResolver = new ClientResolverMemory();
            clinicianManager = new ClinicianManagerMemory();
            administratorManager = new AdministratorManagerMemory();
            authenticationManager = new AuthenticationManagerMemory();
            configManager = new ConfigManagerMemory();
            commandRunner = commandText -> {
                throw new UnsupportedOperationException("Memory storage type does not support running commands.");
            };
            actionResolver = new ActionResolverMemory();
            clinicianResolver = new ClincianResolverMemory();
            administratorResolver = new AdministratorResolverMemory();
            clientFileResolver = new ClientFileResolverMemory();
        } else {
            throw new IllegalArgumentException("DataStorageType cannot be null.");
        }
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

    public static EnumSet<Country> getAllowedCountries() {
        return allowedCountries;
    }

    public static void setAllowedCountries(EnumSet<Country> countries) {
        allowedCountries = countries;
    }

    public static void logout() {
        // Do something with the old session
        session = null;
    }

    public static void setToken(String t) {
        token = t;
    }

    public static String getToken() {
        return token;
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
        return Collections.unmodifiableList(mainControllers);
    }

    public static void clearMainControllers() {
        mainControllers.clear();
    }

    public static void deleteMainController(MainController controller) {
        mainControllers.remove(controller);
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

    public static String getClientEtag() {
        return clientEtag;
    }

    public static void setClientEtag(String etag) {
        System.out.println("Setting client etag to: " + etag);
        clientEtag = etag;
    }

    public static String getClinicianEtag() {
        return clinicianEtag;
    }

    public static void setClinicianEtag(String etag) {
        clinicianEtag = etag;
    }

    public static String getAdministratorEtag() {
        return administratorEtag;
    }

    public static void setAdministratorEtag(String etag) {
        administratorEtag = etag;
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public static void setRestTemplate(RestTemplate template ) {
        restTemplate = template;
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

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static Session getSession() {
        return session;
    }

    public static ClientResolver getClientResolver() {
        return clientResolver;
    }

    public static CommandRunner getCommandRunner() {
        return commandRunner;
    }

    public static ActionResolver getActionResolver() {
        return actionResolver;
    }

    public static ClinicianResolver getClinicianResolver() {
        return clinicianResolver;
    }

    public static AdministratorResolver getAdministratorResolver() {
        return administratorResolver;
    }

    public static ClientFileResolver getClientFileResolver() {
        return clientFileResolver;
    }
}
