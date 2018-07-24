package com.humanharvest.organz.resolvers.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AdministratorResolverRest implements AdministratorResolver {

    @Override
    public Administrator createAdministrator(CreateAdministratorView createAdministratorView) {
        HttpHeaders httpHeaders = createHeaders();

        HttpEntity entity = new HttpEntity<>(createAdministratorView, httpHeaders);

        ResponseEntity<Administrator> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "administrator",
                        HttpMethod.POST,
                        entity,
                        Administrator.class);

        return responseEntity.getBody();
    }

    @Override
    public Administrator modifyAdministrator(Administrator administrator,
            ModifyAdministratorObject modifyAdministratorObject) {

        HttpHeaders httpHeaders = createHeaders();

        HttpEntity entity = new HttpEntity<>(modifyAdministratorObject, httpHeaders);

        ResponseEntity<Administrator> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "administrator",
                        HttpMethod.PATCH,
                        entity,
                        Administrator.class);

        return responseEntity.getBody();

    }

    private HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setETag(State.getAdministratorEtag());
        return httpHeaders;
    }
}
