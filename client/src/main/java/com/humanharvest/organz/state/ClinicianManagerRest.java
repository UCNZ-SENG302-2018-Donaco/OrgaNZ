package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.views.clinician.ModifyClinicianObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ClinicianManagerRest implements ClinicianManager {

    private final RestTemplate restTemplate = State.getRestTemplate();

    private static HttpHeaders newHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.setIfMatch(State.getClinicianEtag());
        httpHeaders.set("X-Auth-Token", State.getToken());
        return httpHeaders;
    }

    /**
     * GET a list of all clinicians.
     * @return all clinicians.
     */
    @Override
    public List<Clinician> getClinicians() {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity<Clinician> entity = new HttpEntity<>(null, httpHeaders);

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


    /**
     * Uses GET to retrieve details of the staff member who's staffId is supplied.
     * @param staffId the id of the staff member who's details are being retrieved
     * @return the details of the clinician found from the supplied staffId.
     */
    @Override
    public Optional<Clinician> getClinicianByStaffId(int staffId) {

        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity<Clinician> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<Clinician> clinician = restTemplate.exchange(State.BASE_URI + "clinicians/{staffId}",
                HttpMethod.GET, entity, Clinician.class, staffId);
        State.setClinicianEtag(clinician.getHeaders().getETag());
        return Optional.ofNullable(clinician.getBody());
    }


    @Override
    public void addClinician(Clinician clinician) {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity<Clinician> entity = new HttpEntity<>(clinician, httpHeaders);
        restTemplate.exchange(State.BASE_URI + "clinicians", HttpMethod.POST, entity, Clinician.class);
    }


    @Override
    public void applyChangesTo(Clinician editedClinician) {
        throw new NotImplementedException();
    }


    @Override
    public void removeClinician(Clinician clinician) {
        HttpHeaders httpHeaders = newHttpHeaders();
        HttpEntity<Clinician> entity = new HttpEntity<>(clinician, httpHeaders);
        restTemplate.exchange(State.BASE_URI + "clinicians/{staffId}", HttpMethod.DELETE, entity, Clinician.class,
                clinician.getStaffId());
    }

    @Override
    public boolean doesStaffIdExist(int staffId) {
        return getClinicians().stream().anyMatch(clinician -> clinician.getStaffId() == staffId);
    }

    @Override
    public Clinician getDefaultClinician() {
        return getClinicianByStaffId(0).orElseThrow(IllegalStateException::new);
    }

    @Override
    public void setClinicians(Collection<Clinician> clinicians) {
        //Do nothing method
        throw new UnsupportedOperationException();
    }
}
