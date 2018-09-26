package com.humanharvest.organz.views.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRecord;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonView;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class TransplantRecordView {

    private TransplantRecord transplantRecord;
    private Client donor;
    private Client receiver;

    TransplantRecordView() {
    }

    public TransplantRecordView(TransplantRecord transplantRecord) {
        this.transplantRecord = transplantRecord;
        this.donor = transplantRecord.getDonor();
        this.receiver = transplantRecord.getReceiver();
    }

    public TransplantRecord getTransplantRecord() {
        transplantRecord.getOrgan().setDonor(donor);

        transplantRecord.setClient(receiver);
        transplantRecord.getRequest().setClient(receiver);
        if (transplantRecord.isCompleted()) {
            transplantRecord.getOrgan().setReceiver(receiver);
        }
        return transplantRecord;
    }
}
