package com.humanharvest.organz.commands.modify;

import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.state.State;

import picocli.CommandLine.Command;

@Command(name = "redo", description = "Redo an undone change.")
public class Redo implements Runnable {

    private ActionInvoker invoker;


    public Redo() {
        invoker = State.getInvoker();
    }

    public Redo(ActionInvoker invoker) {
        this.invoker = invoker;
    }

    public void run() {
        System.out.println(invoker.redo());
    }
}
