package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.DeleteClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "deleteclient", description = "Deletes a client.")
public class DeleteClient implements Runnable {

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    public DeleteClient(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClientManager();
    }

    public DeleteClient(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    @Option(names = {"-u", "--uid"}, description = "User ID", required = true)
    private int uid;

    @Option(names = "-y", description = "Confirms you would like to execute the removal")
    private boolean yes;

    @Override
    public void run() {
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            outputStream.println("No client exists with that user ID");
        } else if (!yes) {
            outputStream.println(
                    String.format("Removing client: %s, with date of birth: %s,\nto proceed please rerun the command "
                                    + "with the -y flag",
                            client.get().getFullName(),
                            client.get().getDateOfBirth()));
        } else {
            Action action = new DeleteClientAction(client.get(), manager);

            outputStream.println(invoker.execute(action));
        }
    }
}
