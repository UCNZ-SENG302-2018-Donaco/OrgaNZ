package com.humanharvest.organz.views.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;

public class ClientLoginResponse {
    @JsonView(Views.Details.class)
    private final String token;

    @JsonInclude(Include.NON_NULL)
    @JsonView(Views.Details.class)
    private Client userData;

    public ClientLoginResponse(String token) {
        this.token = token;
    }

    @JsonCreator
    public ClientLoginResponse(
            @JsonProperty("token") String token,
            @JsonProperty("userData") Client userData) {
        this.token = token;
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public Client getUserData() {
        return userData;
    }
}
