package com.humanharvest.organz.resolvers.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;

public interface ClientResolver {

    //------------GETs----------------

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

    //------------POSTs----------------

    List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request);

    Client markClientAsDead(Client client, LocalDate dateOfDeath);

    List<IllnessRecord> addIllnessRecord(Client client, CreateIllnessView createIllnessView);

    List<MedicationRecord> addMedicationRecord(Client client, CreateMedicationRecordView recordView);

    //------------PATCHs----------------

    TransplantRequest resolveTransplantRequest(
            Client client,
            ResolveTransplantRequestObject request,
            int transplantRequestIndex);

    Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject);

    //------------DELETEs----------------

    void deleteIllnessRecord(Client client, IllnessRecord record);

}