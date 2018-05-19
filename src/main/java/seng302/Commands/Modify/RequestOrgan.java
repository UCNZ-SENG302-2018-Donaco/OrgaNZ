package seng302.Commands.Modify;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddTransplantRequestAction;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;

import seng302.Utilities.Enums.Organ;
import seng302.Utilities.TypeConverters.OrganConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A command to allow admins to request an organ for a specified client.
 */
@Command(name = "requestorgan", description = "Creates an organ request for a user.")
public class RequestOrgan implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;
    private Client client;

    public RequestOrgan() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public RequestOrgan(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-o", "organ", "organType"}, description = "Organ type", converter = OrganConverter.class)
    private Organ organType;

    @Option(names = {"-u", "--uid"}, description = "User ID of user organ being requested", required = true)
    private int uid;


    /**
     * Runs the request organ command
     */
    public void run() {
        // requestorgan -u 1 -o liver
        State.getClientManager().addClient(new Client("Jan", "Michael", "Vincent", LocalDate.now(), 1));
        client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");

        } else if (organType == null) {
            System.out.println("The type of organ to be donated has not been defined.");
        } else {
            TransplantRequest newRequest = new TransplantRequest(client, organType);
            Action action = new AddTransplantRequestAction(client, newRequest);
            invoker.execute(action);
            System.out.println("Successfully requested a " + organType + " for " + client.getFullName());
        }
    }
}
