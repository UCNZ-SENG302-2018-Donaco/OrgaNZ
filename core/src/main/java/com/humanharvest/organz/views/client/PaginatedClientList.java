package com.humanharvest.organz.views.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.humanharvest.organz.Client;

import java.util.List;

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
