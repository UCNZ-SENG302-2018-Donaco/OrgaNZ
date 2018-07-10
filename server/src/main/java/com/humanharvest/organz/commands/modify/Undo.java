package com.humanharvest.organz.commands.modify;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.State;

import picocli.CommandLine.Command;

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
        System.out.println(invoker.undo());
    }
}
