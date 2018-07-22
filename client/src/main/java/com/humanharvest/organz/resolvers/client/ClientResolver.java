package com.humanharvest.organz.resolvers.client;

import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.ResolveTransplantRequestView;

public interface ClientResolver {

    /**
     * Queries the server for the clients organ donation status.
     * @param client The client to retrieve the data from.
     */
    Map<Organ, Boolean> getOrganDonationStatus(Client client);

    /**
     * Queries the server for the clients transplant requests.
     * @param client The client to retrieve the data from.
     */
    List<TransplantRequest> getTransplantRequests(Client client);

    /**
     * Queries the server for the clients medication records.
     * @param client The client to retrieve the data from.
     */
    List<MedicationRecord> getMedicationRecords(Client client);

    TransplantRequest resolveTransplantRequest(
            Client client,
            ResolveTransplantRequestView request,
            int transplantRequestIndex);
}