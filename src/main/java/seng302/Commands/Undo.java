package seng302.Commands;

import picocli.CommandLine.Command;
import seng302.App;
import seng302.Command.CommandInvoker;

@Command(name = "undo", description = "Undo a change.")
public class Undo implements Runnable {

    private CommandInvoker invoker;


    public Undo() {
        invoker = App.getInvoker();
    }

    public Undo(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public void run() {
        invoker.undo();
    }
}
