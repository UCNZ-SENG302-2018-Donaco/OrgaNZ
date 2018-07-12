package com.humanharvest.organz.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.humanharvest.organz.Clinician;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class ClinicianManagerRest implements ClinicianManager {

    private static final String baseUrl = "http://localhost:8080/";

    @Override
    public void addClinician(Clinician clinician) {

    }

    @Override
    public void setClinicians(Collection<Clinician> clinicians) {

    }

    /**
     * GET a list of all clinicians.
     * @return all clinicians.
     */
    @Override
    public List<Clinician> getClinicians() {

        ResponseEntity<List<Clinician>> clinicianResponse = State.getRestTemplate().exchange(baseUrl +
                "clinicians", HttpMethod.GET, null, new ParameterizedTypeReference<List<Clinician>>() {
        });
        List<Clinician> restClinicians = clinicianResponse.getBody();
        if (restClinicians == null) {
            return null;
        } else {
            return new ArrayList<>(restClinicians);
        }
    }

    @Override
    public void removeClinician(Clinician clinician) {

    }

    @Override
    public void applyChangesTo(Clinician clinician) {

    }

    @Override
    public boolean collisionExists(int staffId) {
        return false;
    }

    /**
     * Uses GET to retrieve details of the staff member who's staffId is supplied.
     * @param staffId the id of the staff member who's details are being retrieved
     * @return the details of the clinician found from the supplied staffId.
     */
    @Override
    public Clinician getClinicianByStaffId(int staffId) {
        ResponseEntity<Clinician> clinician = State.getRestTemplate().exchange(baseUrl + "/clinicians/{staffId}",
                HttpMethod.GET, null, new ParameterizedTypeReference<Clinician>() {
                }, staffId);
        State.setClinicianEtag(clinician.getHeaders().getETag());
        return clinician.getBody();
    }

    @Override
    public Clinician getDefaultClinician() {
        return null;
    }
}
