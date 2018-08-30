package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;

import com.humanharvest.organz.actions.ActionInvoker;

import picocli.CommandLine.Command;

@Command(name = "redo", description = "Redo an undone change.")
public class Redo implements Runnable {

    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    public Redo(ActionInvoker invoker) {
        this.invoker = invoker;
        outputStream = System.out;
    }

    public Redo(PrintStream outputStream, ActionInvoker invoker) {
        this.outputStream = outputStream;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        outputStream.println(invoker.redo());
    }
}
