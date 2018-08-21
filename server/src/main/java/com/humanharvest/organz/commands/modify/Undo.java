package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;

import com.humanharvest.organz.actions.ActionInvoker;
import picocli.CommandLine.Command;

@Command(name = "undo", description = "Undo a change.")
public class Undo implements Runnable {

    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    public Undo(ActionInvoker invoker) {
        this.invoker = invoker;
        outputStream = System.out;
    }

    public Undo(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        outputStream.println(invoker.undo());
    }
}
