package seng302.State;

import java.util.ArrayList;

import seng302.Actions.ActionInvoker;
import seng302.Clinician;
import seng302.Controller.MainController;
import seng302.Donor;

/**
 * A static class to store the current state of the system.
 */
public final class State {

    private static DonorManager donorManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;
    private static Session session;
    private static boolean unsavedChanges = false;
    private static ArrayList<MainController> mainControllers = new ArrayList<>();

    private State() {
    }

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

    public static void login(Donor donor) {
        session = new Session(donor);
    }

    public static void login(Clinician clinician) {
        session = new Session(clinician);
    }

    public static void setUnsavedChanges(boolean changes) {
        unsavedChanges = changes;
    }

    public static boolean isUnsavedChanges() {
        return unsavedChanges;
    }

    public static void logout() {
        // Do something with the old session
        session = null;
    }

    public static void addMainController(MainController mainController) {
        mainControllers.add(mainController);
    }

    public static ArrayList<MainController> getMainControllers() {
        return mainControllers;
    }
}
