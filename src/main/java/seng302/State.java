package seng302;

import seng302.Actions.ActionInvoker;

import java.util.HashMap;
import java.util.Map;

public final class State {
    private static DonorManager donorManager;
    private static ClinicianManager clinicianManager;
    private static ActionInvoker actionInvoker;
    private static Map<String, Object> pageContext;

    private State() {}

    public static void init() {
        actionInvoker = new ActionInvoker();
        donorManager = new DonorManager();
        clinicianManager = new ClinicianManager();
        pageContext = new HashMap<>();
    }

    public static DonorManager getDonorManager() {
        return donorManager;
    }

    public static ClinicianManager getClinicianManager() { return clinicianManager; }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }

    public static Object getPageParam(String key) {
        return pageContext.get(key);
    }

    public static void setPageParam(String key, Object value) {
        pageContext.put(key, value);
    }

    public static void removePageParam(String key) {
        pageContext.remove(key);
    }

    public static void clearPageParams() {
        pageContext.clear();
    }
}
