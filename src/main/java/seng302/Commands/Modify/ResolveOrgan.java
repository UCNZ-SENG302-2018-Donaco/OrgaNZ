package seng302.Commands.Modify;


import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ResolveTransplantRequestAction;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.TransplantRequestStatus;
import seng302.Utilities.Enums.ResolveReason;
import seng302.Utilities.TypeConverters.OrganConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Utilities.TypeConverters.ResolveReasonConverter;

/**
 * A command to allow admins to resolve the organ request of a client if it exists with reasoning of why it is being
 * resolved.
 */
@Command(name = "resolveorgan", description = "Resolves a users organ request.")
public class ResolveOrgan implements Runnable {

    private ClientManager manager;
    private ActionInvoker invoker;
    private Client client;

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
            OrganConverter.class)
    private Organ organType;

    @Option(names = {"-r", "-reason"}, description = "Reason for resolving request", required = true, converter = ResolveReasonConverter.class)
    private ResolveReason resolveReason;

    @Option(names = {"-m", "-message"}, description = "Message for why the request was resolved")
    private String message;


    /**
     * Runs the resolve organ command
     */
    public void run() {
        //resolveorgan -u 1 -o liver -r "input error"
        client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }

        boolean organCurrentlyRequested = false;
        TransplantRequest selectedTransplantRequest = new TransplantRequest(client, Organ.LIVER);

        for (TransplantRequest tr: client.getTransplantRequests()) {
            if (tr.getRequestedOrgan() == organType && tr.getStatus() == TransplantRequestStatus.WAITING) {
                organCurrentlyRequested = true;
                selectedTransplantRequest = tr;
                break;
            }
        }
        if (organCurrentlyRequested) {
            resolveRequest(selectedTransplantRequest);
        } else {
            System.out.println(client.getFullName() + " is not currently requesting this organ.");
        }
    }

    /**
     * Helper function to resolve a request and excecute the action if a valid custom reason has been given.
     * @param selectedTransplantRequest the transplant request potentially being resolved.
     */
    private void resolveRequest(TransplantRequest selectedTransplantRequest) {
        Action action = null;
        if (resolveReason == ResolveReason.COMPLETED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest, TransplantRequestStatus.COMPLETED, "Transplant took place.");

        } else if (resolveReason == ResolveReason.DECEASED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest, TransplantRequestStatus.CANCELLED, "The "
                    + "client has deceased.");

        } else if (resolveReason == ResolveReason.CURED) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest, TransplantRequestStatus.CANCELLED, "The disease was cured.");

        } else if (resolveReason == ResolveReason.ERROR) {
            action = new ResolveTransplantRequestAction(selectedTransplantRequest, TransplantRequestStatus.CANCELLED, "Request was a mistake.");

        } else if (resolveReason == ResolveReason.CUSTOM) {
            if (message != null) {
                action = new ResolveTransplantRequestAction(selectedTransplantRequest, TransplantRequestStatus.CANCELLED,
                        message);
            } else {
                System.out.println("Custom resolve reason must have a message specified for why the organ has been "
                        + "resolved. The request is still active.");
            }
        }
        if (action != null) {
            invoker.execute(action);
            System.out.println("Organ request has successfully been resolved.");
        }
    }
}
