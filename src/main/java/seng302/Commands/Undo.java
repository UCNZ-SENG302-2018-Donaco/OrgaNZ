package seng302.Commands;

import picocli.CommandLine.Command;
import seng302.Actions.ActionInvoker;
import seng302.App;

@Command(name = "undo", description = "Undo a change.")
public class Undo implements Runnable {

    private ActionInvoker invoker;


    public Undo() {
        invoker = App.getInvoker();
    }

    public Undo(ActionInvoker invoker) {
        this.invoker = invoker;
    }

    public void run() {
        invoker.undo();
    }
}
