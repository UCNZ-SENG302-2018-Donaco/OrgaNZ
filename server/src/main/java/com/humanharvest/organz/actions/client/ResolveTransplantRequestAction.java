package com.humanharvest.organz.actions.client;

import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.CANCELLED;
import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.COMPLETED;
import static com.humanharvest.organz.utilities.enums.TransplantRequestStatus.WAITING;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.ClientManager;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

/**
 * A reversible action that will resolve the given transplant request with a given status. This status must be one of
 * the valid {@link ResolveTransplantRequestAction#RESOLVED_STATUSES}.
 */
public class ResolveTransplantRequestAction extends ClientAction {

    private static final Collection<TransplantRequestStatus> RESOLVED_STATUSES = Arrays.asList(
            CANCELLED, COMPLETED
    );

    private TransplantRequest request;
    private TransplantRequestStatus newStatus;
    private String reason;

    /**
     * Creates a new resolve transplant request action for the given request and given new status/reason.
     * @param request The transplant request to resolve.
     * @param newStatus The new status to give the request. Must be one of the valid {@link
     * ResolveTransplantRequestAction#RESOLVED_STATUSES}.
     * @param reason The reason for this request being resolved.
     * @param manager The client manager
     */
    public ResolveTransplantRequestAction(TransplantRequest request, TransplantRequestStatus newStatus, String
            reason, ClientManager manager) {
        super(request.getClient(), manager);
        this.request = request;
        this.newStatus = newStatus;
        this.reason = reason;

        if (!RESOLVED_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("New status must be a valid resolved status.");
        }
    }

    /**
     * Apply all changes to the transplantRequest.
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    protected void execute() {
        super.execute();
        request.setStatus(newStatus);
        request.setResolvedDate(LocalDateTime.now());
        request.setResolvedReason(reason);
        manager.applyChangesTo(request.getClient());
    }

    @Override
    protected void unExecute() {
        super.unExecute();
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
