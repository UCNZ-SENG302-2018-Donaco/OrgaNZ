package com.humanharvest.organz.actions.client;

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
            TransplantRequestStatus.CANCELLED, TransplantRequestStatus.COMPLETED
    );

    private final TransplantRequest request;
    private final TransplantRequestStatus status;
    private final LocalDateTime resolvedTime;
    private final String reason;

    /**
     * Creates a new resolve transplant request action for the given request and given new status/reason.
     *
     * @param request The transplant request to resolve.
     * @param status The new status to give the request. Must be one of the valid {@link
     * ResolveTransplantRequestAction#RESOLVED_STATUSES}.
     * @param reason The reason for this request being resolved.
     * @param resolvedTime The resolved time for this request.
     * @param manager The client manager
     */
    public ResolveTransplantRequestAction(TransplantRequest request,
            TransplantRequestStatus status,
            String reason,
            LocalDateTime resolvedTime,
            ClientManager manager) {
        super(request.getClient(), manager);
        this.request = request;
        this.status = status;
        this.reason = reason;
        this.resolvedTime = resolvedTime;

        if (!RESOLVED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("New status must be a valid resolved status.");
        }
    }

    /**
     * Apply all changes to the transplantRequest.
     *
     * @throws IllegalStateException If no changes were made.
     */
    @Override
    protected void execute() {
        super.execute();
        request.setStatus(status);
        request.setResolvedDateTime(resolvedTime);
        request.setResolvedReason(reason);
        manager.applyChangesTo(request.getClient());
    }

    @Override
    protected void unExecute() {
        super.unExecute();
        request.setStatus(TransplantRequestStatus.WAITING);
        request.setResolvedDateTime(null);
        request.setResolvedReason(null);
        manager.applyChangesTo(request.getClient());
    }

    @Override
    public String getExecuteText() {
        return String.format("Resolved transplant request for '%s' with status '%s'",
                request.getRequestedOrgan(), status);
    }

    @Override
    public String getUnexecuteText() {
        return String.format("Reversed resolution of transplant request for '%s' with status '%s'",
                request.getRequestedOrgan(), status);
    }
}
