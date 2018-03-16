package seng302;

import seng302.Actions.ActionInvoker;

public final class State {
    private static DonorManager donorManager;
    private static ActionInvoker actionInvoker;

    private State() {}

    public static void init() {
        actionInvoker = new ActionInvoker();
        donorManager = new DonorManager();


    }

    public static DonorManager getManager() {
        return donorManager;
    }

    public static ActionInvoker getInvoker() {
        return actionInvoker;
    }
}
