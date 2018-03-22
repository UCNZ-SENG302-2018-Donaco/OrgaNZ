package seng302;

import seng302.Actions.ActionInvoker;

import java.util.HashMap;
import java.util.Map;

/**
 * A static class to store the current state of the system.
 */
public final class State {
    private static DonorManager donorManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;

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
}
