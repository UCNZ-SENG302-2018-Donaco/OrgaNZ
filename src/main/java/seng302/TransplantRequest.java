package seng302;

import java.time.LocalDateTime;

import seng302.Utilities.Enums.Organ;

public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestTime;
    private boolean currentRequest;

    public TransplantRequest(Organ requestedOrgan, boolean currentRequest) {
        this.requestedOrgan = requestedOrgan;
        this.currentRequest = currentRequest;
        requestTime = LocalDateTime.now();
    }

    public void setRequestRemoved(boolean requestRemoved) {
        this.currentRequest = requestRemoved;
    }


    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequstTime() {
        return requestTime;
    }
}
