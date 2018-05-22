package seng302.Commands.Modify;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.DeleteClientAction;
import seng302.Client;
import seng302.HistoryItem;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.Utilities.JSONConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "deleteclient", description = "Deletes a client.")
public class DeleteClient implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;

    public DeleteClient() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public DeleteClient(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-u", "--uid"}, description = "User ID", required = true)
    private int uid;

    @Option(names = "-y", description = "Confirms you would like to execute the removal")
    private boolean yes;

    public void run() {
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
        } else if (!yes) {
            System.out.println(
                    String.format("Removing client: %s, with date of birth: %s,\nto proceed please rerun the command "
                                    + "with the -y flag",
                            client.getFullName(),
                            client.getDateOfBirth()));
        } else {
            Action action = new DeleteClientAction(client, manager);

            System.out.println(invoker.execute(action));
            System.out.println("This removal will only be permanent once the 'save' command is used");

            HistoryItem deleteClient = new HistoryItem("DELETE", "Client " + uid + " deleted.");
            JSONConverter.updateHistory(deleteClient, "action_history.json");
        }
    }
}
