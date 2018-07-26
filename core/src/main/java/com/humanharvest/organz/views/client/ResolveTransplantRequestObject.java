package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

public class ResolveTransplantRequestObject {

    private LocalDateTime resolvedDate;
    private TransplantRequestStatus status;
    private String resolvedReason;

    public ResolveTransplantRequestObject() {
    }

    public ResolveTransplantRequestObject(LocalDateTime resolvedDate, TransplantRequestStatus status, String
            resolvedReason) {
        this.resolvedDate = resolvedDate;
        this.status = status;
        this.resolvedReason = resolvedReason;
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
