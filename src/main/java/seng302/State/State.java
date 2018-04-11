package seng302.State;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.Person;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    private static PersonManager personManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;
    private static Session session;

    private State() {
    }

    /**
     * Initialises a new action invoker, person manager and clinician manager.
     */
    public static void init() {
        actionInvoker = new ActionInvoker();
        personManager = new PersonManager();
        clinicianManager = new ClinicianManager();
    }

    public static PersonManager getPersonManager() {
        return personManager;
    }

    public static ClinicianManager getClinicianManager() {
        return clinicianManager;
    }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }

    public static Session getSession() {
        return session;
    }

    public static void login(Session.UserType userType, Object user) {
        if (userType == Session.UserType.PERSON) {
            session = new Session((Person) user);
        } else if (userType == Session.UserType.CLINICIAN) {
            session = new Session((Clinician) user);
        }
    }

    public static void logout() {
        // Do something with the old session
        session = null;
    }
}
