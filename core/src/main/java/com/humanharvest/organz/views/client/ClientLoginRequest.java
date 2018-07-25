package com.humanharvest.organz.views.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientLoginRequest {
    private final int id;

    @JsonCreator
    public ClientLoginRequest(
            @JsonProperty("id") int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
