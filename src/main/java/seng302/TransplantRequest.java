package seng302;

import java.time.LocalDateTime;

import seng302.Utilities.Enums.Organ;

public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestTime;

    public TransplantRequest(Organ requestedOrgan) {
        this.requestedOrgan = requestedOrgan;
        requestTime = LocalDateTime.now();
    }


    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequstTime() {
        return requestTime;
    }
}
