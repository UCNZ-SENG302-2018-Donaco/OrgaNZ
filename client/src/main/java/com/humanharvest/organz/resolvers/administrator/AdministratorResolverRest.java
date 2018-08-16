package com.humanharvest.organz.resolvers.administrator;

import com.humanharvest.organz.Administrator;
import com.humanharvest.organz.HistoryItem;
import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.administrator.CreateAdministratorView;
import com.humanharvest.organz.views.administrator.ModifyAdministratorObject;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class AdministratorResolverRest implements AdministratorResolver {

    @Override
    public Administrator createAdministrator(CreateAdministratorView administratorView) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        HttpEntity<CreateAdministratorView> entity = new HttpEntity<>(administratorView, httpHeaders);

        ResponseEntity<Administrator> responseEntity = State.getRestTemplate().exchange
                (State.getBaseUri() + "administrators",
                        HttpMethod.POST,
                        entity,
                        Administrator.class);

        return responseEntity.getBody();
    }

    @Override
    public Administrator modifyAdministrator(Administrator administrator,
            ModifyAdministratorObject modifyAdministratorObject) {

        HttpHeaders httpHeaders = createHeaders();

        HttpEntity<ModifyAdministratorObject> entity = new HttpEntity<>(modifyAdministratorObject, httpHeaders);

        ResponseEntity<Administrator> responseEntity = State.getRestTemplate().exchange
                (State.getBaseUri() + "administrators",
                        HttpMethod.PATCH,
                        entity,
                        Administrator.class);

        return responseEntity.getBody();

    }

    @Override
    public List<HistoryItem> getHistory() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity<Object> entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<List<HistoryItem>> responseEntity = State.getRestTemplate()
                .exchange(
                        State.getBaseUri() + "history",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<HistoryItem>>() {
                        });

        return responseEntity.getBody();
    }

    private static HttpHeaders createHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setETag(State.getAdministratorEtag());
        return httpHeaders;
    }
}
