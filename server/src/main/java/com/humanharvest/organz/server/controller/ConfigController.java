package com.humanharvest.organz.server.controller;

import java.util.EnumSet;
import java.util.Set;

import com.humanharvest.organz.Hospital;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler.InvalidRequestException;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
import com.humanharvest.organz.utilities.validators.HospitalValidator;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    /**
     * The GET endpoint for getting the allowed countries that clients and clinicians can set
     *
     * @return Response entity containing an EnumSet of the allowed countries
     */
    @GetMapping("/config/countries")
    public ResponseEntity<EnumSet<Country>> getCountries()
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        Set<Country> countries = State.getConfigManager().getAllowedCountries();
        EnumSet<Country> countryEnumSet = EnumSet.noneOf(Country.class);
        countryEnumSet.addAll(countries);

        return new ResponseEntity<>(countryEnumSet, HttpStatus.OK);
    }

    /**
     * The POST endpoint for setting the list of allowed countries
     *
     * @param authToken authentication token - the allowed countries may only be set by an administrator
     * @param countries EnumSet of countries to set as the allowed countries
     * @return response entity containing the http status code
     */
    @PostMapping("/config/countries")
    public ResponseEntity postCountries(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @RequestBody EnumSet<Country> countries)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        State.getAuthenticationManager().verifyAdminAccess(authToken);
        State.getConfigManager().setAllowedCountries(countries);

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * The GET endpoint for getting the hospitals.
     *
     * @param authToken authentication token - hospitals are only accessible to clinicians (or admins)
     * @return Response entity containing an Set of the hospitals
     */
    @GetMapping("/config/hospitals")
    public ResponseEntity<Set<Hospital>> getHospitals(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        // Check the user is a clinician or an admin
        State.getAuthenticationManager().verifyClinicianOrAdmin(authToken);

        Set<Hospital> hospitals = State.getConfigManager().getHospitals();
        return new ResponseEntity<>(hospitals, HttpStatus.OK);
    }

    /**
     * The POST endpoint for setting the list of hospitals
     *
     * @param authToken authentication token - the hospital list may only be set by an administrator
     * @param hospitals Set of hospitals to set
     * @return response entity containing the http status code
     */
    @PostMapping("/config/hospitals")
    public ResponseEntity postHospitals(
            @RequestHeader(value = "X-Auth-Token", required = false) String authToken,
            @RequestBody Set<Hospital> hospitals)
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        // Check the user is an admin and they have sent a valid set of hospitals
        State.getAuthenticationManager().verifyAdminAccess(authToken);
        if (!HospitalValidator.areValid(hospitals)) { // if there are any invalid hospitals in the set
            throw new InvalidRequestException();
        }

        // Set the hospitals to the provided set
        State.getConfigManager().setHospitals(hospitals);

        return new ResponseEntity(HttpStatus.OK);
    }

}
