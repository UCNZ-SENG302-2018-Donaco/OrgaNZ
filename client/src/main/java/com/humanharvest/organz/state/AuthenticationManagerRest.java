package com.humanharvest.organz.state;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.Clinician;
import com.humanharvest.organz.utilities.exceptions.AuthenticationException;
import com.humanharvest.organz.views.administrator.AdministratorLoginRequest;
import com.humanharvest.organz.views.administrator.AdministratorLoginResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class AuthenticationManagerRest implements AuthenticationManager {

    private String token;

    @Override
    public Clinician loginClinician(int staffId, String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Administrator loginAdministrator(String username, String password) throws AuthenticationException {
        AdministratorLoginRequest loginRequest = new AdministratorLoginRequest(username, password);

        try {
            AdministratorLoginResponse response = State.getRestTemplate().postForObject(
                    State.BASE_URI + "login/administrator/", new HttpEntity<>(loginRequest),
                    AdministratorLoginResponse.class);
            token = response.getToken();
            return response.getUserData();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AuthenticationException("Invalid username or password.", e);
            }

            throw e;
        }
    }
}
