package com.humanharvest.organz.views.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.DonatedOrgan;

import java.util.List;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class PaginatedDonatedOrgansList {

    private final List<DonatedOrganView> donatedOrganViews;
    private final int totalResults;

    @JsonCreator
    public PaginatedDonatedOrgansList(
            @JsonProperty("donatedOrgans") List<DonatedOrganView> donatedOrganViews,
            @JsonProperty("totalResults") int totalResults) {
        this.donatedOrganViews = donatedOrganViews;
        this.totalResults = totalResults;
    }

    public List<DonatedOrganView> getDonatedOrgans() {
        return donatedOrganViews;
    }

    public int getTotalResults() {
        return totalResults;
    }

}
