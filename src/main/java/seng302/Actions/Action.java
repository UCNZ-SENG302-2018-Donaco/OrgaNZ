package seng302.Actions;

public interface Action {

    void execute();

    void unExecute();

    String getExecuteText();

    String getUnexecuteText();
}
