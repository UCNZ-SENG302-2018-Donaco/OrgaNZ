package com.humanharvest.organz.views.client;

import java.util.List;

import com.humanharvest.organz.Client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

@JsonView(Views.Overview.class)
public class PaginatedClientList {

    private final List<Client> clients;
    private final int totalResults;

    @JsonCreator
    public PaginatedClientList(
            @JsonProperty("clients") List<Client> clients,
            @JsonProperty("totalResults") int totalResults) {
        this.clients = clients;
        this.totalResults = totalResults;
    }

    public List<Client> getClients() {
        return clients;
    }

    public int getTotalResults() {
        return totalResults;
    }

}
