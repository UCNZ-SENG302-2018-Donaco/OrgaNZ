package com.humanharvest.organz.views.client;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonView;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class DonatedOrganView {

    private DonatedOrgan donatedOrgan;
    private Client donor;
    private Client receiver;

    DonatedOrganView() {
    }

    public DonatedOrganView(DonatedOrgan donatedOrgan) {
        this.donatedOrgan = donatedOrgan;
        this.donor = donatedOrgan.getDonor();
        this.receiver = donatedOrgan.getReceiver();
    }

    public DonatedOrgan getDonatedOrgan() {
        donatedOrgan.setDonor(donor);
        donatedOrgan.setReceiver(receiver);
        return donatedOrgan;
    }
}
