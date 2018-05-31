package com.humanharvest.organz.actions;

/**
 * Abstract class used to represent reversible actions within the system. An Action should hold references to
 * all objects necessary for its execution. Actions should always be executed by an {@link ActionInvoker}.
 */
public abstract class Action {

    /**
     * Executes the action, causing some change(s) within the system.
     */
    protected abstract void execute();

    /**
     * Reverses the action, causing the direct effects of its execution to be undone.
     */
    protected abstract void unExecute();

    /**
     * Returns a message describing the change(s) that this action makes.
     * @return A descriptive message describing the effects of executing this action.
     */
    public abstract String getExecuteText();

    /**
     * Returns a message describing the change(s) that reversing this action makes.
     * @return A descriptive message describing the effects of undoing this action.
     */
    public abstract String getUnexecuteText();
}
