package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.List;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;

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
        loggedInClient = client;
        loggedInUserType = UserType.CLIENT;
    }

    public Session(Clinician clinician) {
        sessionHistory = new ArrayList<>();
        loggedInClinician = clinician;
        loggedInUserType = UserType.CLINICIAN;
    }

    public Session(Administrator administrator) {
        sessionHistory = new ArrayList<>();
        loggedInAdministrator = administrator;
        loggedInUserType = UserType.ADMINISTRATOR;
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
