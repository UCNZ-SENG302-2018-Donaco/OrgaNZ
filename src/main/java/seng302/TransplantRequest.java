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

    private transient Client client;
    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    private RequestStatus status = RequestStatus.WAITING;

    public TransplantRequest(Client client, Organ requestedOrgan) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        this.requestDate = LocalDateTime.now();
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

    public void setClient(Client client) {
        this.client = client;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
