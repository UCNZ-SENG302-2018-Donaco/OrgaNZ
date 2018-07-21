package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Organ;

public class CreateTransplantRequestView {

    private Client client;
    private Organ requestedOrgan;
    private LocalDateTime requestDate;

    public CreateTransplantRequestView() {
    }

    public CreateTransplantRequestView(Client client, Organ requestedOrgan, LocalDateTime requestDate) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        this.requestDate = requestDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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
