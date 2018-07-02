package com.humanharvest.organz.actions.client;

import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.CANCELLED;
import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;

/**
 * A reversible action that will change the client's date of death to the date given, and cancel all their currently
 * pending transplant requests with the reason "The client died.".
 */
public class MarkClientAsDeadAction extends Action {

    private ClientManager manager;
    private Client client;
    private LocalDate deathDate;
    private List<ResolveTransplantRequestAction> resolveTransplantActions;

    /**
     * Creates a new action to mark the given client as dead, with the given date of death.
     * @param client The client to mark as dead.
     * @param deathDate Their date of death.
     * @param manager // TODO
     */
    public MarkClientAsDeadAction(Client client, LocalDate deathDate, ClientManager manager) {
        this.client = client;
        this.deathDate = deathDate;
        this.manager = manager;
        this.resolveTransplantActions = client.getTransplantRequests()
                .stream()
                .filter(request -> request.getStatus() == WAITING)
                .map(request -> new ResolveTransplantRequestAction(request, CANCELLED, "The client died.", manager))
                .collect(Collectors.toList());
    }

    /**
     * Apply all changes to the client and their transplantRequests (all current requests are cancelled).
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    protected void execute() {
        client.setDateOfDeath(deathDate);
        for (ResolveTransplantRequestAction action : resolveTransplantActions) {
            action.execute();
        }
        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        client.setDateOfDeath(null);
        for (ResolveTransplantRequestAction action : resolveTransplantActions) {
            action.unExecute();
        }
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        if (resolveTransplantActions.size() == 0) {
            return String.format("Marked client %d: %s as dead. \n"
                            + "They did not have any pending transplant requests",
                    client.getUid(), client.getFullName());
        }
        String resolvedRequestsText = resolveTransplantActions.stream()
                .map(ResolveTransplantRequestAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Marked client %d: %s as dead. \n"
                        + "These requests were therefore cancelled: \n\n%s",
                client.getUid(), client.getFullName(), resolvedRequestsText);
    }

    @Override
    public String getUnexecuteText() {
        if (resolveTransplantActions.size() == 0) {
            return String.format("Reversed marking client %d: %s as dead. \n"
                            + "They did not have any pending transplant requests",
                    client.getUid(), client.getFullName());
        }

        String resolvedRequestsText = resolveTransplantActions.stream()
                .map(ResolveTransplantRequestAction::getUnexecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed marking client %d: %s as dead. \n"
                        + "These requests were therefore uncancelled: \n\n%s",
                client.getUid(), client.getFullName(), resolvedRequestsText);
    }
}
