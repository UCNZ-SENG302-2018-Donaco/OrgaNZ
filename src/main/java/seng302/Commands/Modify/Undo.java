package seng302.Commands.Modify;

import picocli.CommandLine.Command;
import seng302.Actions.ActionInvoker;
import seng302.State.State;

@Command(name = "undo", description = "Undo a change.")
public class Undo implements Runnable {

    private ActionInvoker invoker;


    public Undo() {
        invoker = State.getInvoker();
    }

    public Undo(ActionInvoker invoker) {
        this.invoker = invoker;
    }

    public void run() {
        invoker.undo();
    }
}
