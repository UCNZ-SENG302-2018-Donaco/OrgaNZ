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
    @OneToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
    private boolean completed;

    protected TransplantRecord() {
    }

    public TransplantRecord(DonatedOrgan organ, TransplantRequest request, Hospital hospital, LocalDate scheduledDate) {
        this.organ = organ;
        this.request = request;
        this.hospital = hospital;
        setDate(scheduledDate);
        setClient(request.getClient());
        setSummary(organ.getOrganType().toString() + " transplant");

        setDescription(String.format(
                "Transplant of %s from donor '%s' to recipient '%s' at %s.",
                organ.getOrganType().toString(),
                organ.getDonor().getFullName(),
                request.getClient().getFullName(),
                hospital.getName()));

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

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public TransplantRecord cloneWithoutId() {
        TransplantRecord clone = new TransplantRecord(getOrgan(), getRequest(), getHospital(), getDate());
        clone.completed = this.completed;
        return clone;
    }
}
