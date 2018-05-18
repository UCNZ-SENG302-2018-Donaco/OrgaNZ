package seng302.Commands.Modify;


import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.ResolveTransplantRequestAction;
import seng302.Client;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.RequestStatus;
import seng302.Utilities.Enums.ResolveReason;
import seng302.Utilities.TypeConverters.OrganConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import seng302.Utilities.TypeConverters.ResolveReasonConverter;

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

    @Option(names = {"-o", "organ", "organType"}, description = "Organ type", required = true, converter = OrganConverter.class)
    private Organ organType;

    @Option(names = {"-r", "-reason"}, description = "Reason for resolving request", required = true, converter = ResolveReasonConverter.class)
    private ResolveReason reason;


    public void run() {
        //resolveorgan -u=1 -o=liver -r="input error"
        client = State.getClientManager().getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");
            return;
        }

        boolean organCurrentlyRequested = false;
        TransplantRequest selectedTransplantRequest = new TransplantRequest(client, Organ.LIVER);

        for (TransplantRequest tr: client.getTransplantRequests()) {
            if (tr.getRequestedOrgan() == organType && tr.getStatus() == RequestStatus.WAITING) {
                organCurrentlyRequested = true;
                selectedTransplantRequest = tr;
                break;
            }
        }
        if (organCurrentlyRequested) {
            // Resolve request
            ResolveReason rs = ResolveReason.CUSTOM;
//            ResolveTransplantRequestAction(selectedTransplantRequest, rs, reason);
//            client.getTransplantRequests();

        } else {
            System.out.println("User is not currently requesting this organ.");
        }
    }
}
