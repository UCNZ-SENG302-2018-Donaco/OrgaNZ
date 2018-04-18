package seng302;

import java.time.LocalDateTime;

import seng302.Utilities.Enums.Organ;

public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private boolean currentRequest;

    public TransplantRequest(Organ requestedOrgan, boolean currentRequest) {
        this.requestedOrgan = requestedOrgan;
        this.currentRequest = currentRequest;
        requestDate = LocalDateTime.now();
    }

    public void setRequestRemoved(boolean currentRequest) {
        this.currentRequest = currentRequest;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public String getRequestTime() {
        return requestDate.toString();
    }

    public String getCurrentRequest() {
        return "" + currentRequest;
    }

}
