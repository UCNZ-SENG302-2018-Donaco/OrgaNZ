package seng302;

import seng302.Actions.ActionInvoker;

import java.util.HashMap;
import java.util.Map;

public final class State {
    private static DonorManager donorManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;

    private State() {}

    public static void init() {
        actionInvoker = new ActionInvoker();
        donorManager = new DonorManager();
        clinicianManager = new ClinicianManager();
    }

    public static DonorManager getDonorManager() {
        return donorManager;
    }

    public static ClinicianManager getClinicianManager() { return clinicianManager; }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }
}
