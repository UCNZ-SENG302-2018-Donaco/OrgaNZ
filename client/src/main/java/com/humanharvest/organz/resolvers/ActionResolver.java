package com.humanharvest.organz.resolvers;

import com.humanharvest.organz.state.State;
import com.humanharvest.organz.views.ActionResponseView;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ActionResolver {

    public static ActionResponseView executeUndo(String ETag) {
        HttpEntity entity = setupEntity(ETag);

        ResponseEntity<ActionResponseView> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "undo", HttpMethod.POST, entity, ActionResponseView.class);
        return responseEntity.getBody();
    }

    public static ActionResponseView executeRedo(String ETag) {
        HttpEntity entity = setupEntity(ETag);

        ResponseEntity<ActionResponseView> responseEntity = State.getRestTemplate().exchange
                (State.BASE_URI + "redo", HttpMethod.POST, entity, ActionResponseView.class);
        return responseEntity.getBody();
    }

    private static HttpEntity setupEntity(String ETag) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        httpHeaders.set("X-Auth-Token", State.getToken());
        httpHeaders.setETag(ETag);

        return new HttpEntity<>(null, httpHeaders);
    }

}
