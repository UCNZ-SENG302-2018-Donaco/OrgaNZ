package com.humanharvest.organz.resolvers.client;

import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;
import org.springframework.beans.BeanUtils;

public class ClientResolverMemory implements ClientResolver {

    @Override
    public Map<Organ, Boolean> getOrganDonationStatus(Client client) {
        return client.getOrganDonationStatus();
    }

    @Override
    public List<TransplantRequest> getTransplantRequests(Client client) {
        return client.getTransplantRequests();
    }

    @Override
    public List<MedicationRecord> getMedicationRecords(Client client) {
        return client.getMedicationRecords();
    }

    @Override
    public TransplantRequest resolveTransplantRequest(Client client, ResolveTransplantRequestView request,
            int transplantRequestIndex) {
        TransplantRequest originalTransplantRequest = client.getTransplantRequests().get(transplantRequestIndex);
        originalTransplantRequest.setStatus(request.getStatus());
        originalTransplantRequest.setResolvedReason(request.getResolvedReason());
        originalTransplantRequest.setResolvedDate(request.getResolvedDate());
        return originalTransplantRequest;
    }

    @Override
    public List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request) {
        TransplantRequest transplantRequest = new TransplantRequest(client, request.getRequestedOrgan());
        client.addTransplantRequest(transplantRequest);
        State.getClientManager().applyChangesTo(client);
        return client.getTransplantRequests();
    }

    @Override
    public Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject) {
        BeanUtils.copyProperties(modifyClientObject, client, modifyClientObject.getUnmodifiedFields());
        return client;
    }
}
