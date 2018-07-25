package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

public class ResolveTransplantRequestObject {

    private TransplantRequest transplantRequest;
    private LocalDateTime resolvedDate;
    private TransplantRequestStatus status;
    private String resolvedReason;

    public ResolveTransplantRequestObject() {
    }

    public ResolveTransplantRequestObject(TransplantRequest transplantRequest,
            LocalDateTime resolvedDate, TransplantRequestStatus status, String resolvedReason) {
        this.transplantRequest = transplantRequest;
        this.resolvedDate = resolvedDate;
        this.status = status;
        this.resolvedReason = resolvedReason;
    }

    public TransplantRequest getTransplantRequest() {
        return transplantRequest;
    }

    public void setTransplantRequest(TransplantRequest transplantRequest) {
        this.transplantRequest = transplantRequest;
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
