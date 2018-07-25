package com.humanharvest.organz.views.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class TransplantRequestView {

    private TransplantRequest transplantRequest;
    private Client client;

    TransplantRequestView() {
    }

    public TransplantRequestView(TransplantRequest transplantRequest) {
        this.transplantRequest = transplantRequest;
        this.client = transplantRequest.getClient();
    }

    public TransplantRequest getTransplantRequest() {
        transplantRequest.setClient(client);
        return transplantRequest;
    }
}
