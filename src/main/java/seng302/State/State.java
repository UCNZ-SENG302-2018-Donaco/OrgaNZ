package seng302.State;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.Donor;

/**
 * A static class to store the current state of the system.
 */
public final class State {
    private static DonorManager donorManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;
    private static Session session;

    private State() {}

    /**
     * Initialises a new action invoker, donor manager and clinician manager.
     */
    public static void init() {
        actionInvoker = new ActionInvoker();
        donorManager = new DonorManager();
        clinicianManager = new ClinicianManager();
    }

    public static DonorManager getDonorManager() {
        return donorManager;
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
        if (userType == Session.UserType.DONOR) {
            session = new Session((Donor) user);
        } else if (userType == Session.UserType.CLINICIAN) {
            session = new Session((Clinician) user);
        }
    }

    public static void logout() {
        // Do something with the old session
        session = null;
    }
}
