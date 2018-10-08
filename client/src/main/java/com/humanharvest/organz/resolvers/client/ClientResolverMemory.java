package com.humanharvest.organz.resolvers.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.humanharvest.organz.Client;
import com.humanharvest.organz.DonatedOrgan;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.IllnessRecord;
import com.humanharvest.organz.MedicationRecord;
import com.humanharvest.organz.ProcedureRecord;
import com.humanharvest.organz.TransplantRecord;
import com.humanharvest.organz.TransplantRequest;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.TransplantRequestStatus;
import com.humanharvest.organz.utilities.exceptions.OrganAlreadyRegisteredException;
import com.humanharvest.organz.views.client.CreateClientView;
import com.humanharvest.organz.views.client.CreateIllnessView;
import com.humanharvest.organz.views.client.CreateMedicationRecordView;
import com.humanharvest.organz.views.client.ModifyClientObject;
import com.humanharvest.organz.views.client.ModifyIllnessObject;
import com.humanharvest.organz.views.client.ModifyProcedureObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;

import org.springframework.beans.BeanUtils;

public class ClientResolverMemory implements ClientResolver {

    private static final Logger LOGGER = Logger.getLogger(ClientResolverMemory.class.getName());

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
        return client.getMedications();
    }

    @Override
    public List<ProcedureRecord> getProcedureRecords(Client client) {
        return client.getProcedures();
    }

    @Override
    public List<IllnessRecord> getIllnessRecords(Client client) {
        return client.getIllnesses();
    }

    @Override
    public Collection<DonatedOrgan> getDonatedOrgans(Client client) {
        return client.getDonatedOrgans();
    }

    @Override
    public List<HistoryItem> getHistory(Client client) {
        return client.getChangesHistory();
    }

    //------------POSTs----------------

    @Override
    public Client createClient(CreateClientView createClientView) {

        //Get the next empty UID
        Optional<Client> nextEmpty = State.getClientManager().getClients()
                .stream()
                .max(Comparator.comparing(Client::getUid));
        int nextId = 0;
        if (nextEmpty.isPresent()) {
            nextId = nextEmpty.get().getUid() + 1;
        }

        Client client = new Client(
                createClientView.getFirstName(),
                createClientView.getMiddleName(),
                createClientView.getLastName(),
                createClientView.getDateOfBirth(),
                nextId);
        State.getClientManager().addClient(client);
        return client;
    }

    @Override
    public List<TransplantRequest> createTransplantRequest(Client client, TransplantRequest transplantRequest) {
        client.addTransplantRequest(transplantRequest);
        State.getClientManager().applyChangesTo(client);
        return client.getTransplantRequests();
    }

    @Override
    public List<IllnessRecord> addIllnessRecord(Client client, CreateIllnessView createIllnessView) {
        IllnessRecord illnessRecord = new IllnessRecord(createIllnessView.getIllnessName(),
                createIllnessView.getDiagnosisDate(),
                createIllnessView.getIsChronic());
        client.addIllnessRecord(illnessRecord);
        State.getClientManager().applyChangesTo(client);
        return client.getIllnesses();
    }

    @Override
    public List<MedicationRecord> addMedicationRecord(Client client, CreateMedicationRecordView medicationRecordView) {
        MedicationRecord medicationRecord = new MedicationRecord(medicationRecordView.getName(),
                LocalDate.now(), null);
        client.addMedicationRecord(medicationRecord);
        State.getClientManager().applyChangesTo(client);
        return client.getMedications();
    }

    @Override
    public List<ProcedureRecord> addProcedureRecord(Client client, ProcedureRecord procedureRecord) {
        client.addProcedureRecord(procedureRecord);
        State.getClientManager().applyChangesTo(client);
        return client.getProcedures();
    }

    @Override
    public List<ProcedureRecord> scheduleTransplantProcedure(DonatedOrgan organ, TransplantRequest request,
            Hospital hospital, LocalDate date) {
        TransplantRecord transplant = new TransplantRecord(organ, request, hospital, date);
        return addProcedureRecord(request.getClient(), transplant);
    }

    @Override
    public DonatedOrgan manuallyOverrideOrgan(DonatedOrgan donatedOrgan, String overrideReason) {
        donatedOrgan.manuallyOverride(overrideReason);
        State.getClientManager().applyChangesTo(donatedOrgan.getDonor());
        return donatedOrgan;
    }

    @Override
    public TransplantRecord completeTransplantRecord(TransplantRecord record) {
        record.setCompleted(true);
        record.getOrgan().setReceiver(record.getClient());
        TransplantRequest request = record.getRequest();
        request.setResolvedReason("The transplant has been completed");
        request.setResolvedDateTime(record.getDate().atTime(LocalTime.now()));
        request.setStatus(TransplantRequestStatus.COMPLETED);

        State.getClientManager().applyChangesTo(record.getClient());
        return record;
    }

    //------------PATCHs----------------

    @Override
    public Map<Organ, Boolean> modifyOrganDonation(Client client, Map<Organ, Boolean> changes) {
        for (Entry<Organ, Boolean> entry : changes.entrySet()) {
            try {
                client.setOrganDonationStatus(entry.getKey(), entry.getValue());
            } catch (OrganAlreadyRegisteredException e) {
                LOGGER.log(Level.INFO, e.getMessage(), e);
            }
        }
        return client.getOrganDonationStatus();
    }

    @Override
    public TransplantRequest resolveTransplantRequest(Client client, TransplantRequest request,
            ResolveTransplantRequestObject resolveTransplantRequestObject) {
        request.setStatus(resolveTransplantRequestObject.getStatus());
        request.setResolvedReason(resolveTransplantRequestObject.getResolvedReason());
        request.setResolvedDateTime(resolveTransplantRequestObject.getResolvedDateTime());
        return request;
    }

    @Override
    public Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject) {
        BeanUtils.copyProperties(modifyClientObject, client, modifyClientObject.getUnmodifiedFields());
        return client;
    }

    @Override
    public MedicationRecord modifyMedicationRecord(Client client, MedicationRecord record, LocalDate stopDate) {
        if (stopDate == null) {
            record.setStarted(LocalDate.now());
            record.setStopped(null);
        } else {
            record.setStopped(LocalDate.now());
        }
        return record;
    }

    @Override
    public IllnessRecord modifyIllnessRecord(Client client, IllnessRecord toModify,
            ModifyIllnessObject modifyIllnessObject) {
        BeanUtils.copyProperties(modifyIllnessObject, toModify, modifyIllnessObject.getUnmodifiedFields());
        if (modifyIllnessObject.getIsChronic() != null) { //quick and dirty fix for chronic not being set
            toModify.setIsChronic(modifyIllnessObject.getIsChronic());
        }
        return toModify;
    }

    @Override
    public ProcedureRecord modifyProcedureRecord(Client client, ProcedureRecord toModify, ModifyProcedureObject
            modifyProcedureObject) {
        BeanUtils.copyProperties(modifyProcedureObject, toModify, modifyProcedureObject.getUnmodifiedFields());
        return toModify;
    }

    @Override
    public DonatedOrgan editManualOverrideForOrgan(DonatedOrgan donatedOrgan, String newOverrideReason) {
        donatedOrgan.manuallyOverride(newOverrideReason);
        State.getClientManager().applyChangesTo(donatedOrgan.getDonor());
        return donatedOrgan;
    }

    //------------DELETEs----------------

    @Override
    public void deleteIllnessRecord(Client client, IllnessRecord record) {
        client.deleteIllnessRecord(record);
    }

    @Override
    public void deleteProcedureRecord(Client client, ProcedureRecord record) {
        client.deleteProcedureRecord(record);
    }

    @Override
    public void deleteMedicationRecord(Client client, MedicationRecord record) {
        client.deleteMedicationRecord(record);
    }

    @Override
    public DonatedOrgan cancelManualOverrideForOrgan(DonatedOrgan donatedOrgan) {
        donatedOrgan.cancelManualOverride();
        State.getClientManager().applyChangesTo(donatedOrgan.getDonor());
        return donatedOrgan;
    }
}
