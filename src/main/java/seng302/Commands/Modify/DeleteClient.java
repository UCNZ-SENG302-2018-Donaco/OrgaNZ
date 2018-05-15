package seng302.Commands.Modify;

import java.util.Scanner;

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

@Command(name = "deleteuser", description = "Deletes a user.")
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

    public void run() {
        Client client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
        } else {
            System.out.println(
                    String.format("Removing user: %s %s %s, with date of birth: %s, would you like to proceed? (y/n)",
                            client.getFirstName(), client.getMiddleName(), client.getLastName(),
                            client.getDateOfBirth()));
            Scanner scanner = new Scanner(System.in);
            String response = scanner.next();

            if (response.equals("y")) {
                Action action = new DeleteClientAction(client, manager);
                invoker.execute(action);

                System.out.println("Client " + uid
                        + " removed. This removal will only be permanent once the 'save' command is used");
                HistoryItem deleteClient = new HistoryItem("DELETE", "Client " + uid + " deleted.");
                JSONConverter.updateHistory(deleteClient, "action_history.json");
            } else {
                System.out.println("User not removed");
            }
        }
    }
}
