package com.humanharvest.organz.views.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.DonatedOrgan;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class PaginatedDonatedOrgansList {

    private final List<DonatedOrgan> donatedOrgans;
    private final int totalResults;

    @JsonCreator
    public PaginatedDonatedOrgansList(
            @JsonProperty("donatedOrgans") List<DonatedOrgan> donatedOrgans,
            @JsonProperty("totalResults") int totalResults) {
        this.donatedOrgans = donatedOrgans;
        this.totalResults = totalResults;
    }

    public List<DonatedOrgan> getDonatedOrgans() {
        return donatedOrgans;
    }

    public int getTotalResults() {
        return totalResults;
    }

}
