package com.humanharvest.organz.commands.modify;

import java.io.PrintStream;
import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.AddTransplantRequestAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.pico_type_converters.PicoOrganConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A command to allow admins to request an organ for a specified client.
 */
@Command(name = "requestorgan", description = "Creates an organ request for a user.")
public class RequestOrgan implements Runnable {

    private final ClientManager manager;
    private final ActionInvoker invoker;
    private final PrintStream outputStream;

    @Option(names = {"-o", "-organ", "-organType"}, description = "Organ type", converter = PicoOrganConverter.class)
    private Organ organType;

    @Option(names = {"-u", "--uid"}, description = "User ID of user organ being requested", required = true)
    private int uid;

    public RequestOrgan(PrintStream outputStream, ActionInvoker invoker) {
        this.invoker = invoker;
        this.outputStream = outputStream;
        manager = State.getClientManager();
    }

    public RequestOrgan(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
        outputStream = System.out;
    }

    /**
     * Runs the request organ command
     */
    @Override
    public void run() {
        // requestorgan -u 1 -o liver
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            outputStream.println("No client exists with that user ID");

        } else if (organType == null) {
            outputStream.println("The type of organ to be donated has not been defined.");

        } else {
            boolean organCurrentlyRequested = false;

            // Iterate through each transplant request for the client,
            // checking if they have already requested this organ
            for (TransplantRequest tr : client.get().getTransplantRequests()) {
                if (tr.getRequestedOrgan() == organType && tr.getStatus() == TransplantRequestStatus.WAITING) {
                    organCurrentlyRequested = true;
                    break;
                }
            }

            if (organCurrentlyRequested) {
                outputStream.println("This organ is already requested.");
            } else {
                TransplantRequest newRequest = new TransplantRequest(client.get(), organType);
                Action action = new AddTransplantRequestAction(client.get(), newRequest, manager);

                outputStream.println(invoker.execute(action));
            }
        }
    }
}
