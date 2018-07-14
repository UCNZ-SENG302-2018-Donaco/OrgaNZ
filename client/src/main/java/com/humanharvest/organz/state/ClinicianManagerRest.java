package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.Clinician;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ClinicianManagerRest implements ClinicianManager {

    @Override
    public void addClinician(Clinician clinician) {
        throw new UnsupportedOperationException();
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
        ResponseEntity<List<Clinician>> clinicianResponse = State.getRestTemplate().exchange(
                State.BASE_URI + "clinicians", HttpMethod.GET, null,
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
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyChangesTo(Clinician clinician) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesStaffIdExist(int staffId) {
        // TODO?
        return false;
    }

    /**
     * Uses GET to retrieve details of the staff member who's staffId is supplied.
     * @param staffId the id of the staff member who's details are being retrieved
     * @return the details of the clinician found from the supplied staffId.
     */
    @Override
    public Clinician getClinicianByStaffId(int staffId) {
        ResponseEntity<Clinician> clinician = State.getRestTemplate().exchange(State.BASE_URI + "clinicians/{staffId}",
                HttpMethod.GET, null, new ParameterizedTypeReference<Clinician>() {
                }, staffId);
        State.setClinicianEtag(clinician.getHeaders().getETag());
        return clinician.getBody();
    }

    @Override
    public Clinician getDefaultClinician() {
        throw new UnsupportedOperationException();
    }
}
