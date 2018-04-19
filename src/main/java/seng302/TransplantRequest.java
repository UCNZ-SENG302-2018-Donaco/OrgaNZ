package seng302;

import java.time.LocalDateTime;

import seng302.Utilities.Enums.Organ;
import java.time.format.DateTimeFormatter;

/**
 * Class to hold Transplant Requests for each time a clinician adds a request for a Client to have a transplant for
 * a specified organ. The Organ type, time of request, and status of request are all recorded.
 */
public class TransplantRequest {

    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private boolean currentRequest;
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy\nh:mm:ss a");

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
