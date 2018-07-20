package com.humanharvest.organz.commands.modify;

import java.util.Optional;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.actions.ActionInvoker;
import com.humanharvest.organz.actions.client.ResolveTransplantRequestAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.ResolveReason;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.pico_type_converters.PicoOrganConverter;
import com.humanharvest.organz.utilities.pico_type_converters.PicoResolveReasonConverter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * A command to allow admins to resolve the organ request of a client if it exists with reasoning of why it is being
 * resolved.
 */
@Command(name = "resolveorgan", description = "Resolves a users organ request.")
public class ResolveOrgan implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;

    public ResolveOrgan() {
        manager = State.getClientManager();
        invoker = State.getInvoker();
    }

    public ResolveOrgan(ClientManager manager, ActionInvoker invoker) {
        this.manager = manager;
        this.invoker = invoker;
    }

    @Option(names = {"-u", "--uid"}, description = "User ID of user organ being requested", required = true)
    private int uid;

    @Option(names = {"-o", "-organ", "-organType"}, description = "Organ type", required = true, converter =
            PicoOrganConverter.class)
    private Organ organType;

    @Option(names = {"-r", "-reason"}, description = "Reason for resolving request", required = true,
            converter = PicoResolveReasonConverter.class)
    private ResolveReason resolveReason;

    @Option(names = {"-m", "-message"}, description = "Message for why the request was resolved")
    private String message;

    /**
     * Runs the resolve organ command
     */
    public void run() {
        //resolveorgan -u 1 -o liver -r "input error"
        Optional<Client> client = manager.getClientByID(uid);
        if (!client.isPresent()) {
            System.out.println("No client exists with that user ID");
            return;
        }

        boolean organCurrentlyRequested = false;
        TransplantRequest selectedTransplantRequest = new TransplantRequest(client.get(), Organ.LIVER);

        for (TransplantRequest tr : client.get().getTransplantRequests()) {
            if (tr.getRequestedOrgan() == organType && tr.getStatus() == TransplantRequestStatus.WAITING) {
                organCurrentlyRequested = true;
                selectedTransplantRequest = tr;
                break;
            }
        }
        if (organCurrentlyRequested) {
            resolveRequest(selectedTransplantRequest);
        } else {
            System.out.println(client.get().getFullName() + " is not currently requesting this organ.");
        }
    }

    /**
     * Helper function to resolve a request and excecute the action if a valid custom reason has been given.
     * @param selectedTransplantRequest the transplant request potentially being resolved.
     */
    private void resolveRequest(TransplantRequest selectedTransplantRequest) {
        Action action = null;
        if (resolveReason == ResolveReason.COMPLETED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest,
                    TransplantRequestStatus.COMPLETED,
                    "Transplant took place.",
                    manager);

        } else if (resolveReason == ResolveReason.DECEASED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest,
                    TransplantRequestStatus.CANCELLED,
                    "The client has deceased.",
                    manager);

        } else if (resolveReason == ResolveReason.CURED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest,
                    TransplantRequestStatus.CANCELLED,
                    "The disease was cured.",
                    manager);

        } else if (resolveReason == ResolveReason.ERROR) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest,
                    TransplantRequestStatus.CANCELLED,
                    "Request was a mistake.",
                    manager);

        } else if (resolveReason == ResolveReason.CUSTOM) {
            if (message != null) {
                action = new ResolveTransplantRequestAction(selectedTransplantRequest,
                        TransplantRequestStatus.CANCELLED,
                        message,
                        manager);
            } else {
                System.out.println("Custom resolve reason must have a message specified for why the organ has been "
                        + "resolved. The request is still active.");
            }
        }
        if (action != null) {
            System.out.println(invoker.execute(action));
        }
    }
}
