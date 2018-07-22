package com.humanharvest.organz.resolvers.client;

import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;

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
}
