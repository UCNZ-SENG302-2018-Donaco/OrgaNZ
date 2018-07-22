package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

public class ResolveTransplantRequestView {

    private Client client;
    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    private TransplantRequestStatus status;
    private String resolvedReason;

    public ResolveTransplantRequestView() {
    }

    public ResolveTransplantRequestView(Client client, Organ requestedOrgan, LocalDateTime requestDate,
            LocalDateTime resolvedDate, TransplantRequestStatus status, String resolvedReason) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        this.requestDate = requestDate;
        this.resolvedDate = resolvedDate;
        this.status = status;
        this.resolvedReason = resolvedReason;
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

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public TransplantRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TransplantRequestStatus status) {
        this.status = status;
    }

    public String getResolvedReason() {
        return resolvedReason;
    }

    public void setResolvedReason(String resolvedReason) {
        this.resolvedReason = resolvedReason;
    }
}
