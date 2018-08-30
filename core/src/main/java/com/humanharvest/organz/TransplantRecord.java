package com.humanharvest.organz;

import java.time.LocalDate;

public class TransplantRecord extends ProcedureRecord {

    private DonatedOrgan organ;
    private TransplantRequest request;
    private boolean completed;

    public TransplantRecord() {
    }

    public TransplantRecord(DonatedOrgan organ, TransplantRequest request, LocalDate scheduledDate) {
        this.organ = organ;
        this.request = request;
        setDate(scheduledDate);
        setClient(request.getClient());
    }

    public DonatedOrgan getOrgan() {
        return organ;
    }

    public TransplantRequest getRequest() {
        return request;
    }

    public Client getDonor() {
        return organ.getDonor();
    }

    public Client getReceiver() {
        return getClient();
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
