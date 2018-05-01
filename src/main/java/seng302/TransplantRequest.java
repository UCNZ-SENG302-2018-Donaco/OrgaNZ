package seng302;

import java.time.LocalDateTime;

import seng302.Utilities.Enums.Organ;

/**
 * Represents a request for a client to receive a transplant for a given organ.
 */
public class TransplantRequest {

    public enum RequestStatus {
        WAITING,
        CANCELLED,
        COMPLETED
    }

    private Client client;
    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    private RequestStatus status = RequestStatus.WAITING;
    private String resolvedReason;

    public TransplantRequest(Client client, Organ requestedOrgan) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        this.requestDate = LocalDateTime.now();
    }

    /**
     * Should only be used by ResolveTransplantRequestAction
     * @return reason that the transplant request was resolved
     */
    public String getResolvedReason() {
        return resolvedReason;
    }

    /**
     * Should only be used by ResolveTransplantRequestAction
     * @param resolvedReason reason that the transplant request was resolved
     */
    public void setResolvedReason(String resolvedReason) {
        this.resolvedReason = resolvedReason;
    }

    public Client getClient() {
        return client;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
