package seng302.Commands.Modify;

import java.time.LocalDate;

import seng302.Actions.Action;
import seng302.Actions.ActionInvoker;
import seng302.Actions.Client.AddTransplantRequestAction;
import seng302.Client;
import seng302.Clinician;
import seng302.State.ClientManager;
import seng302.State.State;
import seng302.TransplantRequest;
import seng302.TransplantRequest.RequestStatus;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.TypeConverters.OrganConverter;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

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

    @Option(names = {"-request", "-req"}, description = "Request organ transplant flag")
    private Boolean requestFlag;

    @Option(names = {"-resolve", "-res"}, description = "Resolve organ request flag")
    private Boolean resolveFlag;

    @Option(names = {"-m"}, description = "Reason for resolving request")
    private String message;


    public void run() {
        State.getClientManager().addClient(new Client("t", "t", "t", LocalDate.now(), 1));
        client = manager.getClientByID(uid);
        if (client == null) {
            System.out.println("No client exists with that user ID");

        } else {
            if (requestFlag != null && resolveFlag != null) {
                System.out.println("Invalid command, cannot request and resolve an organ at once.");

            } else if (requestFlag != null) {
                makeRequest();

            } else if (resolveFlag != null) {
                resolveRequest();

            } else {
                System.out.println("Specify whether organ is being requested or resolved.");
            }

        }
    }

    private void makeRequest() {
        if (organType == null) {
            System.out.println("The type of organ to be donated has not been defined.");
        } else {
            TransplantRequest newRequest = new TransplantRequest(client, organType);
            Action action = new AddTransplantRequestAction(client, newRequest);
            invoker.execute(action);
            System.out.println("Successfully requested a " + organType + " for " + client.getFullName());
        }
    }

    private void resolveRequest() {
        boolean organCurrentlyRequested = false;
        for (TransplantRequest tr: client.getTransplantRequests()) {
            if (tr.getRequestedOrgan() == organType && tr.getStatus() == RequestStatus.WAITING) {
                organCurrentlyRequested = true;
                break;
            }
        }
        if (organCurrentlyRequested) {
            // Resolve request.

        } else {
            System.out.println("User is not currently requesting this organ.");
        }
    }
}
