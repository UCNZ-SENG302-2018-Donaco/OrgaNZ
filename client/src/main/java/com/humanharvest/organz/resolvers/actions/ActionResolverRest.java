package com.humanharvest.organz.resolvers.actions;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.ActionResponseView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ActionResolverRest implements ActionResolver {

    public ActionResponseView executeUndo(String ETag) {
        HttpEntity entity = setupEntity(ETag);

        ResponseEntity<ActionResponseView> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "undo", HttpMethod.POST, entity, ActionResponseView.class);
        return responseEntity.getBody();
    }

    public ActionResponseView executeRedo(String ETag) {
        HttpEntity entity = setupEntity(ETag);

        ResponseEntity<ActionResponseView> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "redo", HttpMethod.POST, entity, ActionResponseView.class);
        return responseEntity.getBody();
    }

    public ActionResponseView getUndo() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());

        HttpEntity entity = new HttpEntity<>(null, httpHeaders);

        ResponseEntity<ActionResponseView> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "undo", HttpMethod.GET, entity, ActionResponseView.class);
        return responseEntity.getBody();
    }

    private HttpEntity setupEntity(String ETag) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setETag(ETag);

        return new HttpEntity<>(null, httpHeaders);
    }

}