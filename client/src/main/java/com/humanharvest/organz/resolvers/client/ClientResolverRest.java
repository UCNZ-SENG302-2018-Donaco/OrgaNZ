package com.humanharvest.organz.resolvers.client;

import com.humanharvest.organz.views.client.ModifyIllnessObject;
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
import com.humanharvest.organz.views.client.ModifyProcedureObject;
import com.humanharvest.organz.views.client.ResolveTransplantRequestObject;
import com.humanharvest.organz.views.client.SingleDateView;
import org.apache.http.protocol.HTTP;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ClientResolverRest implements ClientResolver {

    //------------GETs----------------

    @Override
    public Map<Organ, Boolean> getOrganDonationStatus(Client client) {
        HttpHeaders httpHeaders = createHeaders(false);
        ResponseEntity<Map<Organ, Boolean>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/donationStatus",
                HttpMethod.GET,
                new ParameterizedTypeReference<Map<Organ, Boolean>>() {
                }, client.getUid());

        client.setOrganDonationStatus(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<TransplantRequest> getTransplantRequests(Client client) {
        HttpHeaders httpHeaders = createHeaders(false);
        ResponseEntity<List<TransplantRequest>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/transplantRequests",
                HttpMethod.GET,
                new ParameterizedTypeReference<List<TransplantRequest>>() {
                }, client.getUid());

        client.setTransplantRequests(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<MedicationRecord> getMedicationRecords(Client client) {
        HttpHeaders httpHeaders = createHeaders(false);
        ResponseEntity<List<MedicationRecord>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/medications",
                HttpMethod.GET,
                new ParameterizedTypeReference<List<MedicationRecord>>() {
                }, client.getUid());

        client.setMedicationHistory(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<ProcedureRecord> getProcedureRecords(Client client) {
        HttpHeaders httpHeaders = createHeaders(false);
        httpHeaders.setETag(State.getClientEtag());

        ResponseEntity<List<ProcedureRecord>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/procedures",
                HttpMethod.GET,
                new ParameterizedTypeReference<List<ProcedureRecord>>() {
                }, client.getUid());
        return responseEntity.getBody();
    }

    //------------POSTs----------------

    @Override
    public List<TransplantRequest> createTransplantRequest(Client client, CreateTransplantRequestView request) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<List<TransplantRequest>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/transplantRequests",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<TransplantRequest>>() {
                }, client.getUid());
        return responseEntity.getBody();
    }

    @Override
    public Client markClientAsDead(Client client, LocalDate dateOfDeath) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<Client> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/dead",
                HttpMethod.POST,
                new SingleDateView(dateOfDeath),
                Client.class,
                client.getUid());
        return responseEntity.getBody();
    }

    @Override
    public List<IllnessRecord> addIllnessRecord(Client client, CreateIllnessView createIllnessView) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<List<IllnessRecord>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/illnesses",
                HttpMethod.POST,
                createIllnessView,
                new ParameterizedTypeReference<List<IllnessRecord>>() {
                }, client.getUid());

        client.setIllnessHistory(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<MedicationRecord> addMedicationRecord(Client client, CreateMedicationRecordView medicationRecordView) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<List<MedicationRecord>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/medications",
                HttpMethod.POST,
                medicationRecordView,
                new ParameterizedTypeReference<List<MedicationRecord>>() {
                }, client.getUid());

        client.setMedicationHistory(responseEntity.getBody());
        return responseEntity.getBody();
    }

    @Override
    public List<ProcedureRecord> addProcedureRecord(Client client, CreateProcedureView procedureView) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<List<ProcedureRecord>> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/procedures",
                HttpMethod.POST,
                procedureView,
                new ParameterizedTypeReference<List<ProcedureRecord>>() {
                }, client.getUid());

        return responseEntity.getBody();
    }

    //------------PATCHs----------------

    @Override
    public TransplantRequest resolveTransplantRequest(Client client, ResolveTransplantRequestObject request,
            int transplantRequestIndex) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<TransplantRequest> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/transplantRequests/{requestIndex}",
                HttpMethod.PATCH,
                request,
                TransplantRequest.class,
                client.getUid(),
                transplantRequestIndex);

        return responseEntity.getBody();
    }

    @Override
    public Client modifyClientDetails(Client client, ModifyClientObject modifyClientObject) {
        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<Client> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}",
                HttpMethod.PATCH,
                modifyClientObject,
                Client.class,
                client.getUid());

        return responseEntity.getBody();
    }

    @Override
    public MedicationRecord modifyMedicationRecord(Client client, MedicationRecord record, LocalDate stopDate) {

        String modification;
        if (stopDate == null) {
            modification = "start";
        } else {
            modification = "stop";
        }

        // todo needs to be changed to use the id rather than the index once it is working
        int id = client.getMedications().indexOf(record);

        HttpHeaders httpHeaders = createHeaders(true);
        ResponseEntity<MedicationRecord> responseEntity = sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/medications/{medicationId}/" + modification,
                HttpMethod.PATCH,
                MedicationRecord.class,
                client.getUid(),
                id);

        return responseEntity.getBody();
    }


    @Override
    public IllnessRecord modifyIllnessRecord(Client client,IllnessRecord record){
        int id = client.getIllnesses().indexOf(record) + 1;

        HttpHeaders httpHeaders = createHeaders(true);
        HttpEntity<IllnessRecord> entity = new HttpEntity<>(record,httpHeaders);
        ResponseEntity<IllnessRecord> responseEntity = State.getRestTemplate().exchange(
            String.format("%sclients/%d/illnesses/%d",State.BASE_URI,client.getUid(),id),
                HttpMethod.PATCH,entity,IllnessRecord.class);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }
    @Override
    public ProcedureRecord modifyProcedureRecord(Client client, ModifyProcedureObject modifyProcedureObject,
            long procedureRecordId) {
        HttpHeaders httpHeaders = createHeaders(true);

        HttpEntity<ModifyProcedureObject> entity = new HttpEntity<>(modifyProcedureObject, httpHeaders);

        ResponseEntity<ProcedureRecord> responseEntity = State.getRestTemplate().exchange(
                String.format("%sclients/%d/procedures/%d", State.BASE_URI, client.getUid(), procedureRecordId),
                HttpMethod.PATCH, entity, ProcedureRecord.class);

        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity.getBody();
    }

    //------------DELETEs----------------

    @Override
    public void deleteIllnessRecord(Client client, IllnessRecord record) {

        int id = client.getIllnesses().indexOf(record);

        HttpHeaders httpHeaders = createHeaders(true);
        sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/illnesses/{illnessId}",
                HttpMethod.DELETE,
                IllnessRecord.class,
                client.getUid(),
                id);

        client.deleteIllnessRecord(record);
    }

    @Override
    public void deleteProcedureRecord(Client client, ProcedureRecord record) {
        HttpHeaders httpHeaders = createHeaders(true);
        sendQuery(httpHeaders,
                State.BASE_URI + "clients/{id}/procedures/{procedureId}",
                HttpMethod.DELETE,
                ProcedureRecord.class,
                client.getUid(),
                record.getId());
    }

    //------------Templates----------------

    private static HttpHeaders createHeaders(boolean addIfMatch) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if (addIfMatch) {
            httpHeaders.setIfMatch(State.getClientEtag());
        }
        return httpHeaders;
    }

    private static <T> ResponseEntity<T> sendQuery(HttpHeaders httpHeaders, String url, HttpMethod method,
            ParameterizedTypeReference<T> typeReference, Object... uriVariables) {

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<T> responseEntity = State.getRestTemplate().exchange
                (url, method, entity, typeReference, uriVariables);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity;
    }

    private static <T> ResponseEntity<T> sendQuery(HttpHeaders httpHeaders, String url, HttpMethod method,
            Class<T> typeReference, Object... uriVariables) {

        HttpEntity<?> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<T> responseEntity = State.getRestTemplate().exchange
                (url, method, entity, typeReference, uriVariables);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity;
    }

    private static <T, Y> ResponseEntity<T> sendQuery(HttpHeaders httpHeaders, String url, HttpMethod method,
            Y value, ParameterizedTypeReference<T> typeReference, Object... uriVariables) {

        HttpEntity<Y> entity = new HttpEntity<>(value, httpHeaders);

        ResponseEntity<T> responseEntity = State.getRestTemplate().exchange
                (url, method, entity, typeReference, uriVariables);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity;
    }

    private static <T, Y> ResponseEntity<T> sendQuery(HttpHeaders httpHeaders, String url, HttpMethod method,
            Y value, Class<T> typeReference, Object... uriVariables) {

        HttpEntity<Y> entity = new HttpEntity<>(value, httpHeaders);

        ResponseEntity<T> responseEntity = State.getRestTemplate().exchange
                (url, method, entity, typeReference, uriVariables);
        State.setClientEtag(responseEntity.getHeaders().getETag());
        return responseEntity;
    }
}

