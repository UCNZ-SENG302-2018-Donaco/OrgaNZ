package com.humanharvest.organz;

import java.time.LocalDateTime;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents a request for a client to receive a transplant for a given organ.
 */
@Entity
@Table
@Access(AccessType.FIELD)
public class TransplantRequest {

    @Id
    @GeneratedValue
    @JsonView(Views.Overview.class)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "Client_uid")
    @JsonBackReference
    private Client client;
    @JsonView(Views.Overview.class)
    private Organ requestedOrgan;
    @JsonView(Views.Overview.class)
    private LocalDateTime requestDateTime;
    @JsonView(Views.Overview.class)
    private LocalDateTime resolvedDateTime;
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Overview.class)
    private TransplantRequestStatus status = TransplantRequestStatus.WAITING;
    @JsonView(Views.Overview.class)
    @Column(columnDefinition = "text")
    private String resolvedReason;

    protected TransplantRequest() {
    }

    public TransplantRequest(Client client, Organ requestedOrgan) {
        this.client = client;
        this.requestedOrgan = requestedOrgan;
        requestDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    /**
     * This method should be called only when this record is added to/removed from a client's collection.
     *
     * @param client The client to set this record as belonging to.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    public Organ getRequestedOrgan() {
        return requestedOrgan;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public LocalDateTime getResolvedDateTime() {
        return resolvedDateTime;
    }

    public void setResolvedDateTime(LocalDateTime resolvedDateTime) {
        this.resolvedDateTime = resolvedDateTime;
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

    /**
     * Should only be used by ResolveTransplantRequestAction
     *
     * @param resolvedReason reason that the transplant request was resolved
     */
    public void setResolvedReason(String resolvedReason) {
        this.resolvedReason = resolvedReason;
    }

    public void resolveRequest(LocalDateTime dateTime, String resolvedReason, TransplantRequestStatus status) {
        this.resolvedDateTime = dateTime;
        this.resolvedReason = resolvedReason;
        this.status = status;
    }
}
