package seng302.State;

import java.util.ArrayList;
import java.util.List;

import seng302.Clinician;
import seng302.Person;
import seng302.HistoryItem;

public class Session {

    public enum UserType {
        PERSON, CLINICIAN
    }

    private Person loggedInPerson;
    private Clinician loggedInClinician;
    private UserType loggedInUserType;
    private List<HistoryItem> sessionHistory;

    public Session(Person person) {
        sessionHistory = new ArrayList<>();
        this.loggedInPerson = person;
        this.loggedInUserType = UserType.PERSON;
    }

    public Session(Clinician clinician) {
        sessionHistory = new ArrayList<>();
        this.loggedInClinician = clinician;
        this.loggedInUserType = UserType.CLINICIAN;
    }

    public Person getLoggedInPerson() {
        return loggedInPerson;
    }

    public Clinician getLoggedInClinician() {
        return loggedInClinician;
    }

    public UserType getLoggedInUserType() {
        return loggedInUserType;
    }
}
