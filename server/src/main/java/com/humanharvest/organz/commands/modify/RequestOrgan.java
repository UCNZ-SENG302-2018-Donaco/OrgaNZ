package com.humanharvest.organz.commands.modify;

import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING;

import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.AddTransplantRequestAction;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.TransplantRequest;

import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.pico_type_converters.PicoOrganConverter;

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

    @Option(names = {"-o", "-organ", "-organType"}, description = "Organ type", converter = PicoOrganConverter.class)
    private Organ organType;

    @Option(names = {"-u", "--uid"}, description = "User ID of user organ being requested", required = true)
    private int uid;


    /**
     * Runs the request organ command
     */
    public void run() {
        // requestorgan -u 1 -o liver
        client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");

        } else if (organType == null) {
            System.out.println("The type of organ to be donated has not been defined.");

        } else {
            boolean organCurrentlyRequested = false;

            for (TransplantRequest tr: client.getTransplantRequests()) {
                if (tr.getRequestedOrgan() == organType && tr.getStatus() == WAITING) {
                    organCurrentlyRequested = true;
                    break;
                }
            }
            if (organCurrentlyRequested) {
                System.out.println("This organ is already requested.");
            } else {
                TransplantRequest newRequest = new TransplantRequest(client, organType);
                Action action = new AddTransplantRequestAction(client, newRequest, manager);

                System.out.println(invoker.execute(action));
            }
        }
    }
}
