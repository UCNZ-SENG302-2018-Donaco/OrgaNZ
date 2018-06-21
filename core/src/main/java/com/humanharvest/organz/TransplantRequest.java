package com.humanharvest.organz;

import java.time.LocalDateTime;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

/**
 * Represents a request for a client to receive a transplant for a given organ.
 */
@Entity
@Table
@Access(AccessType.FIELD)
public class TransplantRequest {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_uid")
    @JsonIgnore
    private Client client;
    private Organ requestedOrgan;
    private LocalDateTime requestDate;
    private LocalDateTime resolvedDate;
    @Enumerated(EnumType.STRING)
    private TransplantRequestStatus status = TransplantRequestStatus.WAITING;
    private String resolvedReason;

    protected TransplantRequest() {
    }

    public TransplantRequest(Client client, Organ requestedOrgan) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        this.requestDate = LocalDateTime.now();
    }

    public Client getClient() {
        return client;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public TransplantRequestStatus getStatus() {
        return status;
    }

    public String getResolvedReason() {
        return resolvedReason;
    }

    /**
     * This method should be called only when this record is added to/removed from a client's collection.
     * Therefore it is package-private so it may only be called from Client.
     * @param client The client to set this record as belonging to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public void setStatus(TransplantRequestStatus status) {
        this.status = status;
    }

    /**
     * Should only be used by ResolveTransplantRequestAction
     * @param resolvedReason reason that the transplant request was resolved
     */
    public void setResolvedReason(String resolvedReason) {
        this.resolvedReason = resolvedReason;
    }
}
