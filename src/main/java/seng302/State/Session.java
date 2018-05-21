package seng302.State;

import java.util.ArrayList;
import java.util.List;

import seng302.Administrator;
import seng302.Client;
import seng302.Clinician;
import seng302.HistoryItem;

public class Session {

    public enum UserType {
        CLIENT, CLINICIAN, ADMINISTRATOR
    }

    private Client loggedInClient;
    private Clinician loggedInClinician;
    private Administrator loggedInAdministrator;
    private UserType loggedInUserType;
    private List<HistoryItem> sessionHistory;

    public Session(Client client) {
        sessionHistory = new ArrayList<>();
        this.loggedInClient = client;
        this.loggedInUserType = UserType.CLIENT;
    }

    public Session(Clinician clinician) {
        sessionHistory = new ArrayList<>();
        this.loggedInClinician = clinician;
        this.loggedInUserType = UserType.CLINICIAN;
    }

    public Session(Administrator administrator) {
        sessionHistory = new ArrayList<>();
        this.loggedInAdministrator = administrator;
        this.loggedInUserType = UserType.ADMINISTRATOR;
    }


    public Client getLoggedInClient() {
        return loggedInClient;
    }

    public Clinician getLoggedInClinician() {
        return loggedInClinician;
    }

    public Administrator getLoggedInAdministrator() {
        return loggedInAdministrator;
    }

    public UserType getLoggedInUserType() {
        return loggedInUserType;
    }
}
