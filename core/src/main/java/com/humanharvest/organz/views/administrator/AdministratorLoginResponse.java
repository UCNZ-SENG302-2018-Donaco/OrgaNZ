package com.humanharvest.organz.views.administrator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.views.client.Views;

public class AdministratorLoginResponse {
    @JsonView(Views.Overview.class)
    private final String token;

    @JsonInclude(Include.NON_NULL)
    @JsonView(Views.Overview.class)
    private Administrator userData;

    public AdministratorLoginResponse(String token) {
        this.token = token;
    }

    @JsonCreator
    public AdministratorLoginResponse(
            @JsonProperty("token") String token,
            @JsonProperty("userData") Administrator userData) {
        this.token = token;
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public Administrator getUserData() {
        return userData;
    }
}
