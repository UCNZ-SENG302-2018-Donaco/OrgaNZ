package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.utilities.JSONConverter;

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
        return State.getClientManager()
            .getClientByID(loggedInClient.getUid())
            .orElseThrow(IllegalStateException::new);
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

    public void addToSessionHistory(HistoryItem historyItem) {
        sessionHistory.add(historyItem);
        JSONConverter.updateHistory(historyItem, "action_history.json");
    }

    public List<HistoryItem> getSessionHistory() {
        return Collections.unmodifiableList(sessionHistory);
    }
}
