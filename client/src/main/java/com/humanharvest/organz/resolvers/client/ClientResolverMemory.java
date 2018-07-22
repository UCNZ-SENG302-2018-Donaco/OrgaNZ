package com.humanharvest.organz.resolvers.client;

import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
}
