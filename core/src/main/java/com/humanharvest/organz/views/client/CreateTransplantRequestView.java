package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.Organ;

public class CreateTransplantRequestView {

    private Organ requestedOrgan;
    private LocalDateTime requestDateTime;

    public CreateTransplantRequestView() {
    }

    public CreateTransplantRequestView(Organ requestedOrgan, LocalDateTime requestDateTime) {
        this.requestedOrgan = requestedOrgan;
        this.requestDateTime = requestDateTime;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public void setRequestedOrgan(Organ requestedOrgan) {
        this.requestedOrgan = requestedOrgan;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }
}
