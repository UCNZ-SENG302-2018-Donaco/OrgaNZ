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

    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    private RequestStatus status = RequestStatus.WAITING;

    public TransplantRequest(Organ requestedOrgan) {
        this.requestedOrgan = requestedOrgan;
        this.requestDate = LocalDateTime.now();
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
