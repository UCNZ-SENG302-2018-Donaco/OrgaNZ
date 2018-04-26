package seng302;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import seng302.State.State;
import seng302.Utilities.Enums.Organ;
import seng302.Utilities.Enums.Region;

/**
 * Class to hold Transplant Requests for each time a clinician adds a request for a Client to have a transplant for
 * a specified organ. The Organ type, time of request, and status of request are all properties of a TransplantRequest.
 */
public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private boolean currentRequest;
    private int clientId;

    public TransplantRequest(Organ requestedOrgan, boolean currentRequest) {
        this.requestedOrgan = requestedOrgan;
        this.currentRequest = currentRequest;
        requestDate = LocalDateTime.now();
        clientId = -1;
    }

    public TransplantRequest(Organ requestedOrgan, boolean currentRequest, int clientId) {
        this.requestedOrgan = requestedOrgan;
        this.currentRequest = currentRequest;
        requestDate = LocalDateTime.now();
        this.clientId = clientId;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public String getRequestDateString() {
        return requestDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public boolean getCurrentRequest() {
        return currentRequest;
    }

    public Client getClient() {
        return State.getClientManager().getClientByID(clientId);
    }

    public void setClient(Client client) {
        this.clientId = client.getUid();
    }

    public String getClientName() {
        Client client = State.getClientManager().getClientByID(clientId);
        return client == null ? null : client.getFullName();
    }

    public Region getClientRegion() {
        Client client = State.getClientManager().getClientByID(clientId);
        return client == null ? null : client.getRegion();
    }
}
