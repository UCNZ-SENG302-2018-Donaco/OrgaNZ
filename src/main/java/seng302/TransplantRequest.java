package seng302;

import java.time.LocalDateTime;
import seng302.Utilities.Enums.Organ;

/**
 * Class to hold Transplant Requests for each time a clinician adds a request for a Client to have a transplant for
 * a specified organ. The Organ type, time of request, and status of request are all properties of a TransplantRequest.
 */
public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private boolean currentRequest;

    public TransplantRequest(Organ requestedOrgan, boolean currentRequest) {
        this.requestedOrgan = requestedOrgan;
        this.currentRequest = currentRequest;
        requestDate = LocalDateTime.now();
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public boolean getCurrentRequest() {
        return currentRequest;
    }
}
