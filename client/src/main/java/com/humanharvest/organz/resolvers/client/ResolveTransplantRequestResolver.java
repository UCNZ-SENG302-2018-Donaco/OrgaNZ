package com.humanharvest.organz.resolvers.client;

import java.util.List;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResolveTransplantRequestResolver {

    private Client client;
    private ResolveTransplantRequestView resolveTransplantRequestView;
    private int transplantRequestIndex;

    public ResolveTransplantRequestResolver(Client client,
            ResolveTransplantRequestView resolveTransplantRequestView, int transplantRequestIndex) {
        this.client = client;
        this.resolveTransplantRequestView = resolveTransplantRequestView;
        this.transplantRequestIndex = transplantRequestIndex;
    }

    public TransplantRequest execute() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setIfMatch(State.getClientEtag());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity entity = new HttpEntity<>(resolveTransplantRequestView, httpHeaders);

        ResponseEntity<TransplantRequest> responseEntity = State.getRestTemplate()
                .exchange(
                        State.BASE_URI + "clients/" + client.getUid() + "/transplantRequests/" + transplantRequestIndex,
                        HttpMethod.PATCH, entity, TransplantRequest.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

}

