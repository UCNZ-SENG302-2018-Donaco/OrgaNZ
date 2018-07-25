package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.Organ;

public class CreateTransplantRequestView {

    private Organ requestedOrgan;
    private LocalDateTime requestDate;

    public CreateTransplantRequestView() {
    }

    public CreateTransplantRequestView( Organ requestedOrgan, LocalDateTime requestDate) {
        this.requestedOrgan = requestedOrgan;
        this.requestDate = requestDate;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public void setRequestedOrgan(Organ requestedOrgan) {
        this.requestedOrgan = requestedOrgan;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}
