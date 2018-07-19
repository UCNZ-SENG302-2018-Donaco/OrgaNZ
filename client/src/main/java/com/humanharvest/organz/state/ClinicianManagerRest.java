package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.Clinician;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ClinicianManagerRest implements ClinicianManager {

    private RestTemplate restTemplate = State.getRestTemplate();

    private HttpHeaders newHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        return httpHeaders;
    }

    @Override
    public void addClinician(Clinician clinician) {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity entity = new HttpEntity<>(clinician, httpHeaders);
        restTemplate.exchange(State.BASE_URI + "clinicians", HttpMethod.POST, entity, Clinician.class);
    }

    @Override
    public void setClinicians(Collection<Clinician> clinicians) {
        throw new UnsupportedOperationException();
    }

    /**
     * GET a list of all clinicians.
     * @return all clinicians.
     */
    @Override
    public List<Clinician> getClinicians() {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<Clinician>> clinicianResponse = restTemplate.exchange(
                State.BASE_URI + "clinicians", HttpMethod.GET, entity,
                new ParameterizedTypeReference<List<Clinician>>() {
        });
        List<Clinician> restClinicians = clinicianResponse.getBody();
        if (restClinicians == null) {
            return Collections.emptyList();
        } else {
            return new ArrayList<>(restClinicians);
        }
    }

    @Override
    public void removeClinician(Clinician clinician) {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity entity = new HttpEntity<>(clinician, httpHeaders);
        restTemplate.exchange(State.BASE_URI + "clinicians/{staffId}", HttpMethod.DELETE, entity, Clinician.class,
                clinician.getStaffId());
    }

    @Override
    public void applyChangesTo(Clinician editedClinician) {
//        Optional<Clinician> clinician = new Optional<Clinician>(editedClinician);
//        editedClinician.getStaffId();
//
//        State.getClinicianManager().getClinicianByStaffId(editedClinician.getStaffId()) = editedClinician;
//        editedClinician = editedClinician;
    }

    @Override
    public boolean doesStaffIdExist(int staffId) {
        return getClinicians().stream().anyMatch(clinician -> clinician.getStaffId() == staffId);
    }

    /**
     * Uses GET to retrieve details of the staff member who's staffId is supplied.
     * @param staffId the id of the staff member who's details are being retrieved
     * @return the details of the clinician found from the supplied staffId.
     */
    @Override
    public Optional<Clinician> getClinicianByStaffId(int staffId) {

        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Clinician> clinician = restTemplate.exchange(State.BASE_URI + "clinicians/{staffId}",
                HttpMethod.GET, entity, Clinician.class, staffId);
        State.setClinicianEtag(clinician.getHeaders().getETag());
        return Optional.ofNullable(clinician.getBody());
    }


    @Override
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(0).get();
    }
}
