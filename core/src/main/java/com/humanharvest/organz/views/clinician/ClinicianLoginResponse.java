package com.humanharvest.organz.views.clinician;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.views.client.Views;

public class ClinicianLoginResponse {
    @JsonView(Views.Details.class)
    private final String token;

    @JsonInclude(Include.NON_NULL)
    @JsonView(Views.Details.class)
    private Clinician userData;

    public ClinicianLoginResponse(String token) {
        this.token = token;
    }

    @JsonCreator
    public ClinicianLoginResponse(
            @JsonProperty("token") String token,
            @JsonProperty("userData") Clinician userData) {
        this.token = token;
        this.userData = userData;
    }

    public String getToken() {
        return token;
    }

    public Clinician getUserData() {
        return userData;
    }
}
