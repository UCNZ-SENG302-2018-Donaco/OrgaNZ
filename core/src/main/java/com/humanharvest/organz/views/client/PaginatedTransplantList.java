package com.humanharvest.organz.views.client;

import java.util.List;
import java.util.stream.Collectors;

import com.humanharvest.organz.TransplantRequest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@JsonView(Views.Overview.class)
public class PaginatedTransplantList {

    private final List<TransplantRequestView> transplantRequestViews;
    private final int totalResults;

    @JsonCreator
    public PaginatedTransplantList(
            @JsonProperty("transplantRequestViews") List<TransplantRequestView> transplantRequestViews,
            @JsonProperty("totalResults") int totalResults) {
        this.transplantRequestViews = transplantRequestViews;
        this.totalResults = totalResults;
    }

    public List<TransplantRequest> getTransplantRequests() {
        return transplantRequestViews.stream()
                .map(TransplantRequestView::getTransplantRequest)
                .collect(Collectors.toList());
    }

    public int getTotalResults() {
        return totalResults;
    }
}
