package seng302.Actions.Client;

import static seng302.TransplantRequest.RequestStatus.*;

import java.util.List;
import java.util.stream.Collectors;

import seng302.Actions.Action;
import seng302.Client;

public class MarkClientAsDeadAction extends Action {

    private Client client;
    private List<ResolveTransplantRequestAction> resolveTransplantActions;

    public MarkClientAsDeadAction(Client client) {
        this.client = client;
        this.resolveTransplantActions = client.getTransplantRequests()
                .stream()
                .filter(request -> request.getStatus() == WAITING)
                .map(request -> new ResolveTransplantRequestAction(request, CANCELLED, "The client died."))
                .collect(Collectors.toList());
    }

    @Override
    protected void execute() {
        for (ResolveTransplantRequestAction action : resolveTransplantActions) {
            action.execute();
        }
    }

    @Override
    protected void unExecute() {
        for (ResolveTransplantRequestAction action : resolveTransplantActions) {
            action.unExecute();
        }
    }

    @Override
    public String getExecuteText() {
        String resolvedRequestsText = resolveTransplantActions.stream()
                .map(ResolveTransplantRequestAction::getExecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Marked client %d: %s as dead. \n"
                        + "These requests were therefore cancelled: \n\n%s",
                client.getUid(), client.getFullName(), resolvedRequestsText);
    }

    @Override
    public String getUnexecuteText() {
        String resolvedRequestsText = resolveTransplantActions.stream()
                .map(ResolveTransplantRequestAction::getUnexecuteText)
                .collect(Collectors.joining("\n"));

        return String.format("Reversed marking client %d: %s as dead. \n"
                        + "These requests were therefore uncancelled: \n\n%s",
                client.getUid(), client.getFullName(), resolvedRequestsText);
    }
}
