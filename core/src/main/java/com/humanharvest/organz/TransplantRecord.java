package com.humanharvest.organz;

import java.time.LocalDate;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@Entity
@Access(AccessType.FIELD)
@DiscriminatorValue("transplant")
@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class TransplantRecord extends ProcedureRecord {

    @OneToOne
    @JoinColumn(name = "organ_id")
    private DonatedOrgan organ;

    @OneToOne
    @JoinColumn(name = "request_id")
    private TransplantRequest request;

    private boolean completed;

    protected TransplantRecord() {
    }

    public TransplantRecord(DonatedOrgan organ, TransplantRequest request, LocalDate scheduledDate) {
        this.organ = organ;
        this.request = request;
        setDate(scheduledDate);
        setClient(request.getClient());
        setSummary(organ.getOrganType().toString() + " transplant");
        setDescription(String.format("Transplant of %s from donor '%s' to recipient '%s'.",
                organ.getOrganType().toString(), organ.getDonor().getFullName(), request.getClient().getFullName()));
        addAffectedOrgan(organ.getOrganType());
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
