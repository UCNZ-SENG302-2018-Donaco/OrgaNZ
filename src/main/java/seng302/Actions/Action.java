package seng302.Actions;

public abstract class Action {

    protected abstract void execute();

    protected abstract void unExecute();

    public abstract String getExecuteText();

    public abstract String getUnexecuteText();
}
