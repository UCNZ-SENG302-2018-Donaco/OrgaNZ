package com.humanharvest.organz.views.client;

import java.time.LocalDateTime;

import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;

public class ResolveTransplantRequestObject {

    private LocalDateTime resolvedDateTime;
    private TransplantRequestStatus status;
    private String resolvedReason;

    public ResolveTransplantRequestObject() {
    }

    public ResolveTransplantRequestObject(LocalDateTime resolvedDateTime, TransplantRequestStatus status, String
            resolvedReason) {
        this.resolvedDateTime = resolvedDateTime;
        this.status = status;
        this.resolvedReason = resolvedReason;
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

    public void setResolvedReason(String resolvedReason) {
        this.resolvedReason = resolvedReason;
    }
}
