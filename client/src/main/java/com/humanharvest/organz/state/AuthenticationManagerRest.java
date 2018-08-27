package com.humanharvest.organz.state;

import java.util.Objects;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.views.administrator.AdministratorLoginRequest;
import com.humanharvest.organz.views.administrator.AdministratorLoginResponse;
import com.humanharvest.organz.views.client.ClientLoginRequest;
import com.humanharvest.organz.views.client.ClientLoginResponse;
import com.humanharvest.organz.views.clinician.ClinicianLoginRequest;
import com.humanharvest.organz.views.clinician.ClinicianLoginResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class AuthenticationManagerRest implements AuthenticationManager {

    @Override
    public Client loginClient(int id) throws AuthenticationException {
        ClientLoginRequest loginRequest = new ClientLoginRequest(id);

        try {
            ClientLoginResponse response = State.getRestTemplate().postForObject(
                    State.getBaseUri() + "login/client/", new HttpEntity<>(loginRequest),
                    ClientLoginResponse.class);
            Objects.requireNonNull(response);

            State.setToken(response.getToken());
            State.login(response.getUserData());
            return response.getUserData();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException("Invalid user id.", e);
            }

            throw e;
        }
    }

    @Override
    public Clinician loginClinician(int staffId, String password) throws AuthenticationException {
        ClinicianLoginRequest loginRequest = new ClinicianLoginRequest(staffId, password);

        try {
            ClinicianLoginResponse response = State.getRestTemplate().postForObject(
                    State.getBaseUri() + "login/clinician/", new HttpEntity<>(loginRequest),
                    ClinicianLoginResponse.class);
            Objects.requireNonNull(response);

            State.setToken(response.getToken());
            State.login(response.getUserData());
            return response.getUserData();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException("Invalid staff id or password.", e);
            }

            throw e;
        }
    }

    @Override
    public Administrator loginAdministrator(String username, String password) throws AuthenticationException {
        AdministratorLoginRequest loginRequest = new AdministratorLoginRequest(username, password);

        try {
            AdministratorLoginResponse response = State.getRestTemplate().postForObject(
                    State.getBaseUri() + "login/administrator/", new HttpEntity<>(loginRequest),
                    AdministratorLoginResponse.class);
            Objects.requireNonNull(response);

            State.setToken(response.getToken());
            State.login(response.getUserData());
            return response.getUserData();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException("Invalid username or password.", e);
            }

            throw e;
        }
    }
}
