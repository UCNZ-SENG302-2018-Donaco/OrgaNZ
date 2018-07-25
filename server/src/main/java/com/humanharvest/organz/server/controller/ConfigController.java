package com.humanharvest.organz.server.controller;

import java.util.EnumSet;

import com.humanharvest.organz.server.exceptions.GlobalControllerExceptionHandler;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.utilities.enums.Country;
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
     * @return Response entity containing an EnumSet of the allowed countries
     */
    @GetMapping("/config/countries")
    public ResponseEntity<EnumSet<Country>> getCountries()
            throws GlobalControllerExceptionHandler.InvalidRequestException {

        return new ResponseEntity<>(State.getConfigManager().getAllowedCountries(), HttpStatus.OK);
    }

    /**
     * The POST endpoint for setting the list of allowed countries
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

        return new ResponseEntity(HttpStatus.CREATED);
    }

}
