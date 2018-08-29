package com.humanharvest.organz;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.DateOutOfBoundsException;

public class TransplantRecord extends ProcedureRecord {

    private DonatedOrgan organ;
    private TransplantRequest request;
    private List<IllnessRecord> relatedIllnesses;
    private boolean completed;

    public TransplantRecord() {
    }

    public TransplantRecord(DonatedOrgan organ, TransplantRequest request, LocalDate scheduledDate) {
        this.organ = organ;
        this.request = request;
        setDate(scheduledDate);
        setClient(request.getClient());
        relatedIllnesses = new ArrayList<>();
    }

    public DonatedOrgan getOrgan() {
        return organ;
    }

    public Client getDonor() {
        return organ.getDonor();
    }

    public Client getReceiver() {
        return getClient();
    }

    public void addRelatedIllness(IllnessRecord illnessRecord) {
        relatedIllnesses.add(illnessRecord);
    }

    public List<IllnessRecord> getRelatedIllnesses() {
        return Collections.unmodifiableList(relatedIllnesses);
    }

    public void setCompleted(boolean completed) throws DateOutOfBoundsException {
        if (completed && !this.completed) {
            if (getDate().isAfter(LocalDate.now())) {
                throw new DateOutOfBoundsException("Cannot complete a transplant when the date is in the future");
            } else {
                this.completed = true;
                request.resolveRequest(getDate().atTime(LocalTime.now()),
                        "The transplant has been completed",
                        TransplantRequestStatus.COMPLETED);
                organ.setReceiver(getClient());
            }
        } else if (!completed) {
            this.completed = false;
        }
    }
}
