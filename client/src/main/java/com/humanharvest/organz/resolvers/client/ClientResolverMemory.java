package com.humanharvest.organz.resolvers.client;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;
import com.humanharvest.organz.views.client.CreateProcedureView;
import com.humanharvest.organz.views.client.CreateTransplantRequestView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;
import org.springframework.beans.BeanUtils;

public class ClientResolverMemory implements ClientResolver {

    //------------GETs----------------

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
    public List<ProcedureRecord> getProcedureRecords(Client client) {return client.getProcedures(); }

    //------------POSTs----------------

    @Override
    public List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request) {
        TransplantRequest transplantRequest = new TransplantRequest(client, request.getRequestedOrgan());
        client.addTransplantRequest(transplantRequest);
        State.getClientManager().applyChangesTo(client);
        return client.getTransplantRequests();
    }

    @Override
    public Client markClientAsDead(Client client, LocalDate dateOfDeath){
        client.markDead(dateOfDeath);
        return client;
    }

    @Override
    public List<IllnessRecord> addIllnessRecord(Client client, CreateIllnessView createIllnessView) {
        IllnessRecord illnessRecord = new IllnessRecord(createIllnessView.getIllnessName(),
                createIllnessView.getDiagnosisDate(),
                createIllnessView.isChronic());
        client.addIllnessRecord(illnessRecord);
        return client.getIllnesses();
    }

    @Override
    public List<MedicationRecord> addMedicationRecord(Client client, CreateMedicationRecordView recordView) {
        MedicationRecord medicationRecord = new MedicationRecord(recordView.getName(),
                LocalDate.now(), null);
        client.addMedicationRecord(medicationRecord);
        return client.getMedications();
    }

    @Override
    public  List<ProcedureRecord> addProcedureRecord(Client client, CreateProcedureView procedureView) {
        ProcedureRecord procedureRecord = new ProcedureRecord(
                procedureView.getSummary(),
                procedureView.getDescription(),
                procedureView.getDate());
        client.addProcedureRecord(procedureRecord);
        return client.getProcedures();
    }

    //------------PATCHs----------------

    @Override
    public TransplantRequest resolveTransplantRequest(Client client, ResolveTransplantRequestObject request,
            int transplantRequestIndex) {
        TransplantRequest originalTransplantRequest = client.getTransplantRequests().get(transplantRequestIndex);
        originalTransplantRequest.setStatus(request.getStatus());
        originalTransplantRequest.setResolvedReason(request.getResolvedReason());
        originalTransplantRequest.setResolvedDate(request.getResolvedDate());
        return originalTransplantRequest;
    }

    @Override
    public Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject) {
        BeanUtils.copyProperties(modifyClientObject, client, modifyClientObject.getUnmodifiedFields());
        return client;
    }

    //------------DELETEs----------------

    @Override
    public void deleteIllnessRecord(Client client, IllnessRecord record) {
        client.deleteIllnessRecord(record);
    }
}
