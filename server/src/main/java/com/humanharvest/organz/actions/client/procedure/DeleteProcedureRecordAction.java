package com.humanharvest.organz.actions.client.procedure;

import java.util.List;
import java.util.stream.Collectors;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.client.ClientAction;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

/**
 * A reversible action that will delete the given procedure record from the given client's medication history.
 */
public class DeleteProcedureRecordAction extends ClientAction {

    private ProcedureRecord record;
    private List<TransplantRequest> requestsToDelete;

    /**
     * Creates a new action to delete an procedure record.
     *
     * @param client The client whose medical history to delete it from.
     * @param record The procedure record to delete.
     * @param manager The ClientManager to apply the changes to
     */
    public DeleteProcedureRecordAction(Client client, ProcedureRecord record, ClientManager manager) {
        super(client, manager);
        this.record = record;
    }

    @Override
    protected void execute() {
        super.execute();
        client.deleteProcedureRecord(record);

        if (record instanceof TransplantRecord) {
            TransplantRecord transplant = (TransplantRecord) record;

            // Set the request's status back to waiting
            transplant.getRequest().setStatus(TransplantRequestStatus.WAITING);

            // Make the organ available again
            transplant.getOrgan().setReceiver(null);
            transplant.getOrgan().setAvailable(true);
            manager.applyChangesTo(transplant.getOrgan());
            manager.applyChangesTo(transplant.getRequest());

            // Delete duplicate requests that have since been made for this
            requestsToDelete = client.getTransplantRequests().stream()
                    .filter(request -> request.getRequestedOrgan().equals(transplant.getOrgan().getOrganType()))
                    .filter(request -> request.getStatus() == TransplantRequestStatus.WAITING)
                    .filter(request -> !request.equals(transplant.getRequest()))
                    .collect(Collectors.toList());
            requestsToDelete.forEach(client::removeTransplantRequest);
        }

        manager.applyChangesTo(client);
    }

    @Override
    protected void unExecute() {
        super.unExecute();

        if (record instanceof TransplantRecord) {
            TransplantRecord transplant = (TransplantRecord) record;
            // Set the request's status back to what it was before
            if (transplant.isCompleted()) {
                System.out.println("Change status back to completed");
                transplant.getRequest().setStatus(TransplantRequestStatus.COMPLETED);
                transplant.getOrgan().setReceiver(transplant.getRequest().getClient());
            } else {
                System.out.println("Change status back to scheduled");
                transplant.getRequest().setStatus(TransplantRequestStatus.SCHEDULED);
            }
            // Make the organ set unavailable again
            transplant.getOrgan().setAvailable(false);
            manager.applyChangesTo(transplant.getOrgan());
            manager.applyChangesTo(transplant.getRequest());

            // Re-add the previously deleted duplicate requests
            requestsToDelete.forEach(client::addTransplantRequest);
        }

        record = record.cloneWithoutId();
        client.addProcedureRecord(record);
        manager.applyChangesTo(client);
    }

    @Override
    public String getExecuteText() {
        return String.format("Removed record for procedure '%s' client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Re-added record for procedure '%s' to client %d: %s.",
                record.getSummary(), client.getUid(), client.getFullName());
    }
}
