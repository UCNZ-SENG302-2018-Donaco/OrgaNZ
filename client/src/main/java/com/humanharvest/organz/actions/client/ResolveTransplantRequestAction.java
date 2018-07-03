package com.humanharvest.organz.actions.client;

import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.CANCELLED;
import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.COMPLETED;
import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.actions.Action;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

/**
 * A reversible action that will resolve the given transplant request with a given status. This status must be one of
 * the valid {@link ResolveTransplantRequestAction#RESOLVED_STATUSES}.
 */
public class ResolveTransplantRequestAction extends Action {

    private static final Collection<TransplantRequestStatus> RESOLVED_STATUSES = Arrays.asList(
            CANCELLED, COMPLETED
    );

    private ClientManager manager;
    private TransplantRequest request;
    private TransplantRequestStatus newStatus;
    private String reason;

    /**
     * Creates a new resolve transplant request action for the given request and given new status/reason.
     * @param request The transplant request to resolve.
     * @param newStatus The new status to give the request. Must be one of the valid {@link
     * ResolveTransplantRequestAction#RESOLVED_STATUSES}.
     * @param reason The reason for this request being resolved.
     */
    public ResolveTransplantRequestAction(TransplantRequest request, TransplantRequestStatus newStatus, String
            reason, ClientManager manager) {
        this.request = request;
        this.newStatus = newStatus;
        this.reason = reason;
        this.manager = manager;

        if (!RESOLVED_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("New status must be a valid resolved status.");
        }
    }

    /**
     * Apply all changes to the transplantRequest.
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    public void execute() {
        request.setStatus(newStatus);
        request.setResolvedDate(LocalDateTime.now());
        request.setResolvedReason(reason);
        manager.applyChangesTo(request.getClient());
    }

    @Override
    public void unExecute() {
        request.setStatus(WAITING);
        request.setResolvedDate(null);
        request.setResolvedReason(null);
        manager.applyChangesTo(request.getClient());
    }

    @Override
    public String getExecuteText() {
        return String.format("Resolved transplant request for '%s' with status '%s'",
                request.getRequestedOrgan(), newStatus);
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed resolution of transplant request for '%s' with status '%s'",
                request.getRequestedOrgan(), newStatus);
    }
}
