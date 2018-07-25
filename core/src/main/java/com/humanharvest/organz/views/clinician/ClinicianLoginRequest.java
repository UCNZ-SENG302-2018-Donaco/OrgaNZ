package com.humanharvest.organz.views.clinician;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClinicianLoginRequest {
    private final int staffId;
    private final String password;

    @JsonCreator
    public ClinicianLoginRequest(
            @JsonProperty("staffId") int staffId,
            @JsonProperty("password") String password) {
        this.staffId = staffId;
        this.password = password;
    }

    public int getStaffId() {
        return staffId;
    }

    public String getPassword() {
        return password;
    }
}
