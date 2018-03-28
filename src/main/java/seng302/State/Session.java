package seng302.State;

import seng302.Clinician;
import seng302.Donor;
import seng302.HistoryItem;

import java.util.ArrayList;
import java.util.List;

public class Session {
    public enum UserType {
        DONOR, CLINICIAN
    }

    private Donor loggedInDonor;
    private Clinician loggedInClinician;
    private UserType loggedInUserType;
    private List<HistoryItem> sessionHistory;

    public Session(Donor donor) {
        sessionHistory = new ArrayList<>();
        this.loggedInDonor = donor;
        this.loggedInUserType = UserType.DONOR;
    }

    public Session(Clinician clinician) {
        sessionHistory = new ArrayList<>();
        this.loggedInClinician = clinician;
        this.loggedInUserType = UserType.CLINICIAN;
    }

    public Donor getLoggedInDonor() {
        return loggedInDonor;
    }

    public Clinician getLoggedInClinician() {
        return loggedInClinician;
    }

    public UserType getLoggedInUserType() {
        return loggedInUserType;
    }
}
